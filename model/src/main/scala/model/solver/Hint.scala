package model.solver

import model.{DecoratedBoard, DecoratedCell, OpenSudokuBoard, Position, Solver, SudokuBoard}

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
  def of(board: DecoratedBoard): Option[Hint] = {
    wrongInputHint(board) orElse nextInputHint(board)
  }

  private def nextInputHint(board: DecoratedBoard): Option[Hint] = {
    val openBoard = board.map(_.toOption)
    val node      = SolverNode.initial(openBoard)
    UniqueOptionInSubset.hint(node) orElse SingleOptionForPosition.hint(node)
  }

  private def wrongInputHint(board: DecoratedBoard): Option[Hint] = {
    val givens = board.map {
      case DecoratedCell.Given(value) => Some(value)
      case _                          => None
    }

    val solution = Solver.perfectSolver(givens).uniqueSolution

    solution.flatMap {
      solution =>
        SudokuBoard
          .positions(board.dim)
          .map {
            pos => (pos, board.get(pos), solution.get(pos))
          }
          .collectFirst {
            case (pos, DecoratedCell.Input(input), solution) if input != solution =>
              WrongInputHint(
                position = pos,
                relatedPositions = SudokuBoard
                  .allSubsetsOf(pos)(board.dim)
                  .flatten
                  .filter(_ != pos)
                  .filter(pos => board.get(pos).toOption.contains(input))
                  .toSet
              )
          }
    }
  }
}
