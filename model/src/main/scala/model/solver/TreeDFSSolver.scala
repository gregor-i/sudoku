package model.solver

import algorithms.TreeTraversal
import model._

private case class Node(board: OpenSudokuBoard, options: OptionsSudokuBoard, openPositions: Set[Position])

private[model] object TreeDFSSolver extends Solver {
  override def apply(puzzle: OpenSudokuBoard): SolverResult = {
    val root = Node(
      puzzle,
      OptionsSudokuBoard(puzzle),
      SudokuBoard.positions(puzzle.dim).filter(puzzle.get(_).isEmpty).toSet
    )

    def children(node: Node): List[Node] =
      node.openPositions
        .minByOption(node.options.get(_).size)
        .toList
        .flatMap { pos =>
          for (option <- node.options.get(pos))
            yield Node(
              node.board.set(pos, Some(option)),
              OptionsSudokuBoard.set(node.options, pos, option),
              node.openPositions - pos
            )
        }

    SolverResult.fromLazyList(
      TreeTraversal.traverseLeaves(root, children).flatMap(t => Validate(t.board))
    )
  }
}
