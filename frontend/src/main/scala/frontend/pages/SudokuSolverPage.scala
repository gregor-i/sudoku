package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, OpenSudokuBoard, Solver, SudokuBoard}
import monocle.macros.Lenses
import org.scalajs.dom.{KeyboardEvent, document}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js

@Lenses
case class SudokuSolverState(
    board: OpenSudokuBoard,
    focus: Option[(Int, Int)]
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
      SudokuSolverState(board, focus = None)
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
        .child(
          Node("div.content-column.is-flex-grow-1")
            .child(
              SudokuBoardSVG(context.local.board.map(_.fold("")(_.toString)), Some(rectInteraction)).classes("is-flex-grow-1")
            )
            .childOptional(contextMenu())
            .child(solveButton())
        )
    }

  private def rectInteraction(implicit context: Context): SudokuBoardSVG.Interaction =
    (pos, node) =>
      node
        .event(
          "click",
          Action(SudokuSolverState.focus.set(Some(pos)))
        )
        .event("dblclick", Action(SudokuSolverState.board.modify(_.set(pos, None))))

  private def globalEventListener(node: Node)(implicit context: Context): Node = {
    val dim = context.local.board.dim
    def setValue(pos: (Int, Int), value: Option[Int]) = {
      val clearFocus = SudokuSolverState.focus.set(None)
      val set        = SudokuSolverState.board.modify(_.set(pos, value))
      context.update((set andThen clearFocus).apply(context.local))
    }

    def rotate(pos: (Int, Int)): (Int, Int) =
      (
        (pos._1 + dim.blockSize) % dim.blockSize,
        (pos._2 + dim.blockSize) % dim.blockSize
      )
    def setFocus(pos: (Int, Int)) =
      context.update(SudokuSolverState.focus.set(Some(rotate(pos)))(context.local))

    object ValidNumber {
      def unapply(str: String): Option[Int] = str.toIntOption.filter(SudokuBoard.values(dim).contains)
    }

    val hook: js.Function0[Unit] = () =>
      document.body.onkeydown = (event: KeyboardEvent) => {
        (event.key, context.local.focus) match {
          case ("Backspace", Some(pos))     => setValue(pos, None)
          case ("Delete", Some(pos))        => setValue(pos, None)
          case (ValidNumber(i), Some(pos))  => setValue(pos, Some(i))
          case ("ArrowUp", Some((x, y)))    => setFocus(x, y - 1)
          case ("ArrowDown", Some((x, y)))  => setFocus(x, y + 1)
          case ("ArrowLeft", Some((x, y)))  => setFocus(x - 1, y)
          case ("ArrowRight", Some((x, y))) => setFocus(x + 1, y)
          case _                            => ()
        }
      }

    node
      .hook("insert", hook)
      .hook("postpatch", hook)
  }

  private def contextMenu()(implicit context: Context): Option[Node] =
    context.local.focus.map { pos =>
      val scale      = 2.5
      val clientRect = document.getElementById(s"cell_${pos._1}_${pos._2}").getBoundingClientRect()
      Node("div.is-overlay")
        .style("background", "rgba(0, 0, 0, 0.2)")
        .style("z-index", "1")
        .event("click", Action(SudokuSolverState.focus.set(None)))
        .child {
          InputNumberSVG(
            context.local.board.dim,
            interaction = Some { (value, node) =>
              node.event(
                "click",
                Action {
                  SudokuSolverState.focus.set(None) andThen
                    SudokuSolverState.board.modify(_.set(pos, Some(value)))
                }
              )
            }
          ).styles(
            Seq(
              "position"   -> "absolute",
              "left"       -> s"min(calc(100vw - ${clientRect.width * scale}px), max(0px, ${clientRect.left - clientRect.width * (scale - 1.0) / 2.0}px))",
              "top"        -> s"min(calc(100vh - ${clientRect.height * scale}px), max(0px, ${clientRect.top - clientRect.height * (scale - 1.0) / 2.0}px))",
              "width"      -> s"${clientRect.width * scale}px",
              "height"     -> s"${clientRect.height * scale}px",
              "background" -> "white",
              "box-shadow" -> "2px 2px 3px 4px rgba(0,0,0,0.2)"
            )
          )
        }
    }

  private def solveButton()(implicit context: Context): Node =
    ButtonList(
      Button("Clear", Icons.clear, Action(SudokuSolverState.board.modify(_.map(_ => None)))),
      Button(
        "Solve",
        Icons.solve,
        Snabbdom.event { _ =>
          val process = Future {
            Solver(context.local.board)
              .take(2)
              .toList
          }

          Toasts.futureToast("solving ...", process) {
            case scala.util.Success(Seq(solution)) =>
              context.update(SolvedSudokuState(solution))
              (frontend.toasts.Success, "solved!")
            case scala.util.Success(Seq()) => (frontend.toasts.Warning, "No solution found. Maybe some numbers are wrong?")
            case scala.util.Success(_)     => (frontend.toasts.Warning, "Multiple solutions found. Maybe some numbers are missing?")
            case scala.util.Failure(_)     => (frontend.toasts.Danger, "Something went wrong ...")
          }
        }
      ).classes("is-primary")
    ).classes("m-2")

}
