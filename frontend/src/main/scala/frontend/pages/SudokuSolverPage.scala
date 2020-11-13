package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Button, ButtonList, Header, SudokuBoardSVG}
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, OpenSudokuBoard, Solver, SudokuBoard}
import monocle.macros.Lenses
import org.scalajs.dom.{KeyboardEvent, document}
import snabbdom.{Node, Snabbdom}

import scala.scalajs.js

@Lenses
case class SudokuSolverState(
    board: OpenSudokuBoard,
    focus: (Int, Int)
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
      SudokuSolverState(board, (0, 0))
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
        .child(solveButton())
    }

  private def rectInteraction(implicit context: Context): SudokuBoardSVG.Interaction =
    (pos, node) =>
      node
        .`class`("active", context.local.focus == pos)
        .event("click", Action(SudokuSolverState.focus.set(pos)))

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
