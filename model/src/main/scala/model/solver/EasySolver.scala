package model.solver

import model._

object EasySolver extends Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult =
    SolvingStrategy.solveWithStrategy(puzzle) { node =>
      SolvingStrategy.singleOptionForPosition(node)
    }
}
