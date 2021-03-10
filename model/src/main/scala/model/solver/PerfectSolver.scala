package model.solver

import algorithms.TreeTraversal
import model._

private[model] object PerfectSolver extends Solver {
  override def apply(puzzle: OpenSudokuBoard): SolverResult = {
    val root = SolverNode.initial(puzzle)

    def children(node: SolverNode): List[SolverNode] =
      node.openPositions
        .minByOption(node.options.get(_).size)
        .toList
        .flatMap { pos =>
          for (option <- node.options.get(pos))
            yield SolverNode.setValue(node, pos, option)
        }

    SolverResult.fromLazyList(
      TreeTraversal.traverseLeaves(root, children).flatMap(t => Validate(t.board))
    )
  }
}
