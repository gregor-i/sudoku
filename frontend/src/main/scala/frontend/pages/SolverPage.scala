package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model._
import monocle.macros.Lenses
import org.scalajs.dom.{KeyboardEvent, document}
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js

@Lenses
case class SolverState(
    board: OpenSudokuBoard,
    focus: Option[Position]
) extends PageState

object SolverState {
  def empty(): SolverState = SolverState(
    board = SudokuBoard.empty(Dimensions(3, 3)),
    focus = None
  )
}

object SudokuSolverPage extends Page[SolverState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp @ QPHelper.OpenSudoku(board)) if qp.get("page").contains("SudokuSolverPage") =>
      SolverState(board, focus = None)
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
          Action(SolverState.focus.set(Some(pos)))
        )
        .event("dblclick", Action(SolverState.board.modify(_.set(pos, None))))

  private def globalEventListener(node: Node)(implicit context: Context): Node = {
    val dim = context.local.board.dim
    def setValue(pos: (Int, Int), value: Option[Int]) = {
      val clearFocus = SolverState.focus.set(None)
      val set        = SolverState.board.modify(_.set(pos, value))
      context.update((set andThen clearFocus).apply(context.local))
    }

    def rotate(pos: (Int, Int)): (Int, Int) =
      (
        (pos._1 + dim.blockSize) % dim.blockSize,
        (pos._2 + dim.blockSize) % dim.blockSize
      )
    def setFocus(pos: (Int, Int)) =
      context.update(SolverState.focus.set(Some(rotate(pos)))(context.local))

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
      InputContextMenu(
        dim = context.local.board.dim,
        reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
        setFocus = pos => context.update(SolverState.focus.set(pos)(context.local)),
        setValue = value =>
          context.update(SolverState.focus.set(None) andThen SolverState.board.modify(_.set(pos, value)) apply context.local)
      )
    }

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button("Clear", Icons.clear, Action(SolverState.board.modify(_.map(_ => None)))),
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
