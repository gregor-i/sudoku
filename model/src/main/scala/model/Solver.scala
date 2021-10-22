package model

import model.SolverResult._
import model.solver._

trait Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult
}

object Solver {
  def perfectSolver: Solver = PerfectSolver
  def mediumSolver: Solver  = MediumSolver
  def easySolver: Solver    = EasySolver

  def shufflingPerfectSolver(seed: Int): Solver = PerfectSolver.withShuffle(seed)

  def forDifficulty(difficulty: Difficulty): Solver =
    difficulty match {
      case Difficulty.Hard   => perfectSolver
      case Difficulty.Medium => mediumSolver
      case Difficulty.Easy   => easySolver
    }
}

sealed trait SolverResult {
  def uniqueSolution: Option[SolvedSudokuBoard] = this match {
    case UniqueSolution(solution) => Some(solution)
    case NoSolution               => None
    case MultipleSolutions(_)     => None
    case CouldNotSolve            => None
  }
}

object SolverResult {
  case object CouldNotSolve                                            extends SolverResult
  case object NoSolution                                               extends SolverResult
  case class UniqueSolution(solution: SolvedSudokuBoard)               extends SolverResult
  case class MultipleSolutions(solutions: LazyList[SolvedSudokuBoard]) extends SolverResult

  def fromLazyList(lazyList: LazyList[SolvedSudokuBoard]): SolverResult = lazyList match {
    case LazyList(solution) => UniqueSolution(solution)
    case LazyList()         => NoSolution
    case list               => MultipleSolutions(list)
  }
}
