package model.solver

import model._

object MediumSolver extends Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult =
    SolvingStrategy.solveWithStrategy(puzzle) { node =>
      SolvingStrategy.singleOptionForPosition(node) ++
        SolvingStrategy.uniqueOptionInSubset(node)
    }
}
