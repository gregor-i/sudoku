package model.solver

import model.{OpenSudokuBoard, Position}

case class Hint(
    position: Position,
    value: Int,
    blockingPositions: Set[Position]
)

object Hint {
  def of(board: OpenSudokuBoard): Option[Hint] = {
    val node = SolverNode.initial(board)
    UniqueOptionInSubset.hint(node) orElse SingleOptionForPosition.hint(node)
  }
}
