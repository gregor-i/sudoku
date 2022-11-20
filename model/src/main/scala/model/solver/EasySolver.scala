package model.solver

import model._

object EasySolver extends Solver {
  def solve(puzzle: OpenSudokuBoard): Option[FilledSudokuBoard] =
    SolvingStrategy.solveWithStrategy(puzzle) {
      node =>
        UniqueOptionInSubset.solve(node).toSet.intersect(SingleOptionForPosition.solve(node).toSet)
    }
}
