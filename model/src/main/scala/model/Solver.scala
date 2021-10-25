package model

import model.solver._

trait Solver {
  def solve(puzzle: OpenSudokuBoard): Option[FilledSudokuBoard]
  def canSolve(puzzle: OpenSudokuBoard): Boolean = solve(puzzle).isDefined
}

object Solver {
  def mediumSolver: Solver = MediumSolver
  def easySolver: Solver   = EasySolver

  def forDifficulty(difficulty: Difficulty): Solver =
    difficulty match {
      case Difficulty.Hard   => HardSolver
      case Difficulty.Medium => mediumSolver
      case Difficulty.Easy   => easySolver
    }
}
