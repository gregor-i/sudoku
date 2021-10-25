package model.solver

import model.{SudokuPuzzle, PuzzleCell, OpenSudokuBoard, Position, Solver, SudokuBoard}

sealed trait Hint
case class NextInputHint(
    position: Position,
    value: Int,
    blockingPositions: Set[Position]
) extends Hint

case class WrongInputHint(
    position: Position,
    relatedPositions: Set[Position]
) extends Hint

object Hint {
  def of(board: SudokuPuzzle): Option[Hint] = {
    wrongInputHint(board) orElse nextInputHint(board)
  }

  private def nextInputHint(board: SudokuPuzzle): Option[Hint] = {
    val openBoard = board.map(_.visible)
    val node      = SolverNode.initial(openBoard)
    UniqueOptionInSubset.hint(node) orElse SingleOptionForPosition.hint(node)
  }

  private def wrongInputHint(board: SudokuPuzzle): Option[Hint] = {
    val givens = board.map {
      case PuzzleCell.Given(value) => Some(value)
      case _                       => None
    }

    SudokuBoard
      .positions(board.dim)
      .map(pos => (pos, board.get(pos)))
      .collectFirst {
        case (pos, PuzzleCell.WrongInput(input, solution)) =>
          WrongInputHint(
            position = pos,
            relatedPositions = SudokuBoard
              .allSubsetsOf(pos)(board.dim)
              .flatten
              .filter(_ != pos)
              .filter(pos => board.get(pos).visible.contains(input))
              .toSet
          )
      }
  }
}
