package frontend.pages

import frontend.components._
import frontend.util.{Action, AsyncUtil}
import frontend.{NoRouting, Page, PageState}
import model.SolverResult.{MultipleSolutions, NoSolution, UniqueSolution}
import model._
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.Node
import snabbdom.components.{Button, ButtonList}
import snabbdom.toasts.{ToastType, Toasts}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.chaining.scalaUtilChainingOps

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

object SudokuSolverPage extends Page[SolverState] with NoRouting {
  override def render(implicit context: Context): Node = {
    val decoratedBoard = DecoratedBoard(context.local.board)
    Node("div.grid-layout.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(decoratedBoard)
              .extendRects(rectInteraction)
              .extendRects(SudokuBoardSVG.wrongNumbers(enabled = true, decoratedBoard))
              .toNode
              .classes("grid-main-svg")
          )
      )
      .child(
        buttonBar().classes("grid-footer")
      )
      .childOptional(contextMenu())
      .pipe(
        InputContextMenu.globalEventListener(
          dim = context.local.board.dim,
          focus = context.local.focus,
          setValue = (pos, value) => {
            val clearFocus = SolverState.focus.set(None)
            val set        = SolverState.board.modify(_.set(pos, value))
            context.update((set andThen clearFocus).apply(context.local))
          },
          setFocus = pos => context.update(SolverState.focus.set(Some(pos))(context.local))
        )
      )
  }

  private def rectInteraction(implicit context: Context): SudokuBoardSVG.Extension =
    (pos, node) => node.event("click", Action(SolverState.focus.set(Some(pos))))

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
    ButtonList
      .right(
        Button("Clear", Icons.clear, Action(SolverState.board.modify(_.map(_ => None)))),
        Button(
          "Solve",
          Icons.solve,
          _ => {
            val process = AsyncUtil.future {
              Solver.perfectSolver(context.local.board)
            }

            Toasts.asyncToast("solving ...", process) {
              case scala.util.Success(UniqueSolution(solution)) =>
                context.update(SolvedSudokuState(solution))
                (ToastType.Success, "solved!")
              case scala.util.Success(NoSolution) =>
                (ToastType.Warning, "No solution found. Maybe some numbers are wrong?")
              case scala.util.Success(MultipleSolutions(_)) =>
                (ToastType.Warning, "Multiple solutions found. Maybe some numbers are missing?")
              case _ =>
                (ToastType.Danger, "Something went wrong ...")
            }
          }
        ).classes("is-primary", "mr-0")
      )
      .classes("my-2")

}
