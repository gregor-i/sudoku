package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, OpenSudokuBoard, Solver, SudokuBoard, Validate}
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
    case (_, "/", qp @ QPHelper.OpenSudoku(board)) if qp.get("page").contains("SudokuSolverPage") =>
      SudokuSolverState(board, focus = None)
    case (_, "/", qp) if qp.isEmpty =>
      SudokuSolverState(SudokuBoard.empty(Dimensions(3, 3)), focus = None)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(("/", Map("page" -> "SudokuSolverPage") ++ QPHelper.OpenSudoku.toQP(state.board)))

  override def render(implicit context: Context): Node = {
    val errorPositions   = Validate.findErrors(context.local.board)
    val boardWithStrings = context.local.board.map(_.fold("")(_.toString))
    globalEventListener {
      Node("div.no-scroll")
        .child(Header.renderHeader())
        .child(
          Node("div.content-column.is-flex-grow-1")
            .child(
              SudokuBoardSVG(boardWithStrings, errorPositions, Some(rectInteraction)).classes("is-flex-grow-1")
            )
            .childOptional(contextMenu())
            .child(buttonBar())
        )
    }
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

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button("Clear", Icons.clear, Action(SudokuSolverState.board.modify(_.map(_ => None)))),
      Button(
        "Solve",
        Icons.solve,
        Snabbdom.event { _ =>
          val process = AsyncUtil.future {
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
      ).classes("is-primary", "mr-0")
    ).classes("my-2")

}
