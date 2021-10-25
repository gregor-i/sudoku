package model.solver

import model.BoardExamples.{easyExample, hardExample, mediumExample}
import model.SolverResult.CouldNotSolve
import model._
import org.scalatest.funsuite.AnyFunSuite

class EasySolverTest extends AnyFunSuite {
  test("solves Sudokus, generated with Difficulty Easy") {
    for (seed <- 0 until 100) {
      val puzzle = Generator(dim = Dimensions(3, 3), seed = seed, difficulty = Difficulty.Easy)

      assert(EasySolver(puzzle.map(_.visible)).uniqueSolution.isDefined)
    }
  }

  test("easy example 3x3 can be solved") {
    val Some(solution) = EasySolver(easyExample).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("medium example 3x3 can not be solved") {
    assert(EasySolver(mediumExample) == CouldNotSolve)
  }

  test("hard example 3x3 can not be solved") {
    assert(EasySolver(hardExample) == CouldNotSolve)
  }

  test("solves very simple boards") {
    val puzzle = BoardExamples.completedBoard.set((0, 0), None)
    assert(EasySolver(puzzle).uniqueSolution.isDefined)
  }
}
