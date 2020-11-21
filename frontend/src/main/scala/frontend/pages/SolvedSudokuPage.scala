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
  private object SolvedSudoku {
    def unapply(qp: Map[String, String]): Option[SolvedSudokuBoard] =
      for {
        width  <- qp.get("width").flatMap(_.toIntOption)
        height <- qp.get("height").flatMap(_.toIntOption)
        dim = Dimensions(width, height)
        board          <- qp.get("board").map(_.replaceAll(",", " ")).flatMap(SudokuBoard.fromString(dim))
        validatedBoard <- Validate(board)
      } yield validatedBoard
  }

  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp @ SolvedSudoku(board)) if qp.get("page").contains("SolvedSudokuPage") =>
      SolvedSudokuState(board)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(
      "/" -> Map(
        "page"   -> "SolvedSudokuPage",
        "width"  -> state.board.dim.width.toString,
        "height" -> state.board.dim.height.toString,
        "board"  -> state.board.data.map(_.toString).mkString(",")
      )
    )

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
