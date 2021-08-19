package model.solver

import model._

object MediumSolver extends Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult =
    SolvingStrategy.solveWithStrategy(puzzle) { node =>
      SingleOptionForPosition.solve(node) ++
        UniqueOptionInSubset.solve(node)
    }
}
