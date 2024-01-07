package frontend

import model.{Position, SudokuPuzzle}
import model.Generator
import model.solver.Hint

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Random

enum PageState {
  case PuzzleState(
      board: SudokuPuzzle,
      focus: Option[Position],
      hint: Option[Hint]
  )

  case Loading(
      process: Future[PageState]
  )
}

object PageState {
  def initial(globalState: GlobalState)(using ExecutionContext): PageState = globalState.lastPuzzle match {
    case None =>
      PageState.Loading(Future {
        val seed  = new Random().nextLong()
        val board = Generator(globalState.dimensions, seed, globalState.difficulty)
        PageState.PuzzleState(board, None, None)
      })
    case Some(lastPuzzle) => PageState.PuzzleState(lastPuzzle, None, None)
  }
}
