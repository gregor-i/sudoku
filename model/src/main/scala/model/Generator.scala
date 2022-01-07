package model

import model.solver.PerfectSolver

import scala.util.Random
import scala.util.chaining.*

object Generator {
  def apply(dim: Dimensions, seed: Long, difficulty: Difficulty, pattern: Pattern = NoPattern): FreshSudokuPuzzle = {
    val random = new Random(seed)

    val fristRow = random.shuffle(SudokuBoard.values(dim))
    val seededBoard =
      SudokuBoard.fill(dim) {
        case (x, 0) => Some(fristRow(x))
        case _      => None
      }

    makePuzzle(
      positions = pattern.groups(dim).pipe(random.shuffle(_)),
      board = PerfectSolver.withShuffle(seed).allSolutions(seededBoard).head.map(PuzzleCell.Given.apply),
      solver = Solver.forDifficulty(difficulty)
    )
  }

  def makePuzzle(
      positions: Seq[Seq[Position]],
      board: FreshSudokuPuzzle,
      solver: Solver
  ): FreshSudokuPuzzle = {
    positions.foldLeft(board) {
      (board, positions) =>
        val reducedBoard =
          positions.foldLeft(board)((board, position) => board.mod(position, cell => PuzzleCell.Empty(cell.solution)))
        if (solver.canSolve(reducedBoard.map(_.visible)))
          reducedBoard
        else
          board
    }
  }
}
