package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.{GlobalState, Page, PageState}
import model.{DecoratedCell, SolvedSudokuBoard, SudokuBoard}
import monocle.macros.Lenses
import snabbdom.{Node, Snabbdom}

@Lenses
case class SolvedSudokuState(
    board: SolvedSudokuBoard
) extends PageState

object SolvedSudokuPage extends Page[SolvedSudokuState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = PartialFunction.empty
  override def stateToUrl(state: State): Option[(Path, QueryParameter)]                      = None

  override def render(implicit context: Context): Node =
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.is-flex-grow-1")
          .child(
            SudokuBoardSVG(context.local.board.map(DecoratedCell.Input), None).classes("is-flex-grow-1")
          )
          .child(buttonBar())
      )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button(
        "Clear",
        Icons.clear,
        Snabbdom.event(_ => context.update(SolverState(SudokuBoard.empty(context.local.board.dim), None)))
      ).classes("is-primary", "mr-0")
    ).classes("my-2")
}
