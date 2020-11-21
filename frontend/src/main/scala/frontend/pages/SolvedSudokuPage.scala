package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Button, ButtonList, Header, Icons, SudokuBoardSVG}
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, SolvedSudokuBoard, SudokuBoard, Validate}
import monocle.macros.Lenses
import snabbdom.{Node, Snabbdom}

@Lenses
case class SolvedSudokuState(
    board: SolvedSudokuBoard
) extends PageState

object SolvedSudokuPage extends Page[SolvedSudokuState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp @ QPHelper.SolvedSudoku(board)) if qp.get("page").contains("SolvedSudokuPage") =>
      SolvedSudokuState(board)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(("/", Map("page" -> "SolvedSudokuPage") ++ QPHelper.SolvedSudoku.toQP(state.board)))

  override def render(implicit context: Context): Node =
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.is-flex-grow-1")
          .child(
            SudokuBoardSVG(context.local.board.map(_.toString), Set.empty, None).classes("is-flex-grow-1")
          )
          .child(buttonBar())
      )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button(
        "Clear",
        Icons.clear,
        Snabbdom.event(_ => context.update(SudokuSolverState(SudokuBoard.empty(context.local.board.dim), None)))
      ).classes("is-primary", "mr-0")
    ).classes("my-2")
}
