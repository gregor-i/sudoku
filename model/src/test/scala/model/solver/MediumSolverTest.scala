package model.solver

import model.BoardExamples.{easyExample, hardExample}
import model.Validate
import org.scalatest.funsuite.AnyFunSuite

class MediumSolverTest extends AnyFunSuite {

  test("easy example 3x3 can be solved") {
    val Some(solution) = MediumSolver.solve(easyExample)
    assert(Validate.correct(solution))
  }

  test("medium example 3x3 can be solved") {
    val Some(solution) = MediumSolver.solve(easyExample)
    assert(Validate.correct(solution))
  }

  test("hard example 3x3 can not be solved") {
    assert(MediumSolver.solve(hardExample) == None)
  }
}
