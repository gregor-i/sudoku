package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Button, ButtonList, Header, SudokuInput}
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, Solver, SudokuBoard, OpenSudokuBoard}
import monocle.macros.Lenses
import snabbdom.{Node, Snabbdom}

@Lenses
case class SudokuSolverState(
    board: OpenSudokuBoard
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
      SudokuSolverState(board)
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
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(SudokuInput(SudokuSolverState.board).classes("grower"))
      .child(solveButton())

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
