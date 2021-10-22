package model
package inifinit

import model.SolvedSudokuBoard

object ContinuationOptions {
  def apply(board: SolvedSudokuBoard, area: Set[Position], seed: Int): LazyList[SolvedSudokuBoard] = {
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
