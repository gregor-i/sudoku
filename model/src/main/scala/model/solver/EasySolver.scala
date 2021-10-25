package model.solver

import model._

object EasySolver extends Solver {
  def solve(puzzle: OpenSudokuBoard): Option[FilledSudokuBoard] =
    SolvingStrategy.solveWithStrategy(puzzle) {
      SingleOptionForPosition.solve
    }
}
