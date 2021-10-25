package model
package inifinit

import model.FilledSudokuBoard

object ContinuationOptions {
  def apply(board: FilledSudokuBoard, area: Set[Position], seed: Int): LazyList[FilledSudokuBoard] = {
    val cleared = SudokuBoard.fill(board.dim) {
      pos =>
        if (area.contains(pos))
          None
        else
          Some(board.get(pos))
    }

    Solver.shufflingPerfectSolver(seed)(cleared) match {
      case SolverResult.MultipleSolutions(solutions) => solutions
      case SolverResult.UniqueSolution(solution)     => LazyList(solution)
      // fixme: the solver will never produce `CouldNotSolve`. it should not be handled here.
      case SolverResult.CouldNotSolve => LazyList.empty
      case SolverResult.NoSolution    => LazyList.empty
    }
  }

}
