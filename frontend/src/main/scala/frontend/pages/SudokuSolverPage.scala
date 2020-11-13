package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Button, ButtonList, Header, InputNumberSVG, SudokuBoardSVG}
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, OpenSudokuBoard, Solver, SudokuBoard}
import monocle.macros.Lenses
import org.scalajs.dom
import org.scalajs.dom.html.Element
import org.scalajs.dom.raw.ClientRect
import org.scalajs.dom.{KeyboardEvent, document}
import org.w3c.dom.html.HTMLElement
import snabbdom.{Node, Snabbdom}

import scala.scalajs.js
import scala.util.chaining._

@Lenses
case class SudokuSolverState(
    board: OpenSudokuBoard,
    focus: (Int, Int),
    contextMenu: Option[((Int, Int), ClientRect)]
) extends PageState

object SudokuSolverPage extends Page[SudokuSolverState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp) =>
      val width  = qp.get("width").flatMap(_.toIntOption)
      val height = qp.get("height").flatMap(_.toIntOption)
      val board = (width, height) match {
        case (Some(width), Some(height)) =>
          val dim = Dimensions(width, height)
          qp.get("board")
            .map(_.replaceAll(",", " "))
            .flatMap(SudokuBoard.fromString(dim))
            .getOrElse(SudokuBoard.empty(dim))
        case _ =>
          SudokuBoard.empty(Dimensions(3, 3))
      }
      SudokuSolverState(board, focus = (0, 0), contextMenu = None)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(
      "/" -> Map(
        "width"  -> state.board.dim.width.toString,
        "height" -> state.board.dim.height.toString,
        "board"  -> state.board.data.map(_.fold("_")(_.toString)).mkString(",")
      )
    )

  override def render(implicit context: Context): Node =
    globalEventListener {
      Node("div.no-scroll")
        .child(Header.renderHeader())
        .child(SudokuBoardSVG(context.local.board, Some(rectInteraction)).classes("grower"))
        .childOptional(contextMenu())
        .child(solveButton())
    }

  private def rectInteraction(implicit context: Context): SudokuBoardSVG.Interaction =
    (pos, node) =>
      node
        .`class`("active", context.local.focus == pos)
        .event(
          "click",
          Snabbdom.event { event =>
            if (context.local.board.get(pos).isEmpty) {
              val bounding = event.currentTarget.asInstanceOf[Element].getBoundingClientRect()
              context.update(SudokuSolverState.contextMenu.set(Some((pos, bounding)))(context.local))
            }
          }
        )

  private def globalEventListener(node: Node)(implicit context: Context): Node = {
    val dim = context.local.board.dim
    def setValue(pos: (Int, Int), value: Option[Int]) =
      context.update(SudokuSolverState.board.modify(_.set(pos, value))(context.local))

    def rotate(pos: (Int, Int)): (Int, Int) =
      (
        (pos._1 + dim.blockSize) % dim.blockSize,
        (pos._2 + dim.blockSize) % dim.blockSize
      )
    def setFocus(pos: (Int, Int)) =
      context.update(SudokuSolverState.focus.set(rotate(pos))(context.local))

    object ValidNumber {
      def unapply(str: String): Option[Int] = str.toIntOption.filter(SudokuBoard.values(dim).contains)
    }

    val hook: js.Function0[Unit] = () =>
      document.body.onkeydown = (event: KeyboardEvent) => {
        (event.key, context.local.focus) match {
          case ("Backspace", pos)     => setValue(pos, None)
          case ("Delete", pos)        => setValue(pos, None)
          case (ValidNumber(i), pos)  => setValue(pos, Some(i))
          case ("ArrowUp", (x, y))    => setFocus(x, y - 1)
          case ("ArrowDown", (x, y))  => setFocus(x, y + 1)
          case ("ArrowLeft", (x, y))  => setFocus(x - 1, y)
          case ("ArrowRight", (x, y)) => setFocus(x + 1, y)
          case _                      => ()
        }
      }

    node
      .hook("insert", hook)
      .hook("postpatch", hook)
  }

  private def contextMenu()(implicit context: Context): Option[Node] =
    context.local.contextMenu.map {
      case (pos, clientRect) =>
        Node("div")
          .styles(
            Seq(
              "position" -> "absolute",
              "left"     -> s"0",
              "top"      -> s"0",
              "right"    -> s"0",
              "bottom"   -> s"0"
            )
          )
          .event("click", Action(SudokuSolverState.contextMenu.set(None)))
          .child {
            InputNumberSVG(
              context.local.board.dim,
              interaction = Some { (value, node) =>
                node.event(
                  "click",
                  Snabbdom.event(
                    _ =>
                      (SudokuSolverState.contextMenu.set(None) andThen
                        SudokuSolverState.board.modify(_.set(pos, Some(value))))
                        .apply(context.local)
                        .pipe(context.update)
                  )
                )
              }
            ).styles(
              Seq(
                "position" -> "absolute",
                "left"     -> s"${clientRect.left.toString}px",
                "top"      -> s"${clientRect.top.toString}px",
                "width"    -> s"${clientRect.width.toString}px",
                "height"   -> s"${clientRect.height.toString}px"
              )
            )
          }
    }

  private def solveButton()(implicit context: Context): Node =
    ButtonList(
      Button(
        "Solve",
        Snabbdom.event { _ =>
          Solver(context.local.board) match {
            case Seq(solution) => println(s"solution: ${solution.data}")
            case Seq()         => println("no solution")
            case _             => println("multiple solutions")
          }
        }
      )
    )

}
