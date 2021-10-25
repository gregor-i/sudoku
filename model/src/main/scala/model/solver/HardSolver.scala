package model.solver

import model.{FilledSudokuBoard, OpenSudokuBoard, Solver}

object HardSolver extends Solver {
  def solve(puzzle: OpenSudokuBoard): Option[FilledSudokuBoard] =
    PerfectSolver.uniqueSolution(puzzle)
}
