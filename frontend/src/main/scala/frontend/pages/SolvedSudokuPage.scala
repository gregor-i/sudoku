package frontend.pages

import frontend.components._
import frontend.{NoRouting, Page, PageState}
import model.{DecoratedCell, SolvedSudokuBoard, SudokuBoard}
import monocle.macros.Lenses
import snabbdom.Node
import snabbdom.components.{Button, ButtonList}

@Lenses
case class SolvedSudokuState(
    board: SolvedSudokuBoard
) extends PageState

object SolvedSudokuPage extends Page[SolvedSudokuState] with NoRouting {
  override def render(implicit context: Context): Node =
    Node("div.grid-layout.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(context.local.board.map(DecoratedCell.Input), extension = None, highlightMistakes = false)
              .classes("grid-main-svg")
          )
      )
      .child(buttonBar().classes("grid-footer"))

  private def buttonBar()(implicit context: Context): Node =
    ButtonList
      .right(
        Button(
          "Clear",
          Icons.clear,
          _ => context.update(SolverState(SudokuBoard.empty(context.local.board.dim), None))
        ).classes("is-primary", "mr-0")
      )
      .classes("my-2")
}
