package model.solver

import model.*
import model.BoardExamples.{easyExample, hardExample, mediumExample}
import org.scalatest.funsuite.AnyFunSuite

class EasySolverTest extends AnyFunSuite {
  test("solves Sudokus, generated with Difficulty Easy") {
    for (seed <- 0 until 100) {
      val puzzle = Generator(dim = Dimensions(3, 3), seed = seed, difficulty = Difficulty.Easy)

      assert(EasySolver.canSolve(puzzle.map(_.visible)))
    }
  }

  test("easy example 3x3 can be solved") {
    val Some(solution) = EasySolver.solve(easyExample)
    assert(Validate.correct(solution))
  }

  test("medium example 3x3 can not be solved") {
    assert(EasySolver.solve(mediumExample) == None)
  }

  test("hard example 3x3 can not be solved") {
    assert(EasySolver.solve(hardExample) == None)
  }

  test("solves very simple boards") {
    val puzzle = BoardExamples.completedBoard.set((0, 0), None)
    assert(EasySolver.canSolve(puzzle))
  }
}
