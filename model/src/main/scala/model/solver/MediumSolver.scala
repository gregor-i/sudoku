package model.solver

import model._

object MediumSolver extends Solver {
  def solve(puzzle: OpenSudokuBoard): Option[FilledSudokuBoard] =
    SolvingStrategy.solveWithStrategy(puzzle) {
      node =>
        SingleOptionForPosition.solve(node) ++
          UniqueOptionInSubset.solve(node)
    }
}
