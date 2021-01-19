package model

import model.SolverResult._
import model.solver._

trait Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult
}

object Solver {
  def solver: Solver = TreeDFSSolver
}

sealed trait SolverResult {
  def uniqueSolution: Option[SolvedSudokuBoard] = this match {
    case UniqueSolution(solution) => Some(solution)
    case NoSolution               => None
    case MultipleSolutions(_)     => None
  }
}

object SolverResult {
  case object NoSolution                                               extends SolverResult
  case class UniqueSolution(solution: SolvedSudokuBoard)               extends SolverResult
  case class MultipleSolutions(solutions: LazyList[SolvedSudokuBoard]) extends SolverResult

  def fromLazyList(lazyList: LazyList[SolvedSudokuBoard]): SolverResult = lazyList match {
    case LazyList(solution) => UniqueSolution(solution)
    case LazyList()         => NoSolution
    case list               => MultipleSolutions(list)
  }
}
