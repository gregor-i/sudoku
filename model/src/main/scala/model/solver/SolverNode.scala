package model.solver

import model.{OpenSudokuBoard, OptionsSudokuBoard, Position, SudokuBoard}

private[solver] case class SolverNode(board: OpenSudokuBoard, options: OptionsSudokuBoard, openPositions: Set[Position])

private[solver] object SolverNode {
  def initial(puzzle: OpenSudokuBoard): SolverNode =
    SolverNode(
      puzzle,
      OptionsSudokuBoard(puzzle),
      SudokuBoard.positions(puzzle.dim).filter(puzzle.get(_).isEmpty).toSet
    )

  def setValue(node: SolverNode, pos: Position, option: Int): SolverNode = {
    SolverNode(
      board = node.board.set(pos, Some(option)),
      options = OptionsSudokuBoard.set(node.options, pos, option),
      openPositions = node.openPositions - pos
    )
  }
}
