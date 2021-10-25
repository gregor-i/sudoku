package model
package infinite

import model.FilledSudokuBoard
import model.solver.PerfectSolver

object ContinuationOptions {
  def apply(board: FilledSudokuBoard, area: Set[Position], seed: Int): LazyList[FilledSudokuBoard] = {
    val cleared = SudokuBoard.fill(board.dim) {
      pos =>
        if (area.contains(pos))
          None
        else
          Some(board.get(pos))
    }

    PerfectSolver.withShuffle(seed).allSolutions(cleared)
  }

}
