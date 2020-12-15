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
    Node("div.grid-layout")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(context.local.board.map(DecoratedCell.Input), None).classes("grid-main-svg")
          )
      )
      .child(buttonBar().classes("grid-footer"))

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button(
        "Clear",
        Icons.clear,
        Snabbdom.event(_ => context.update(SolverState(SudokuBoard.empty(context.local.board.dim), None)))
      ).classes("is-primary", "mr-0")
    ).classes("my-2")
}
