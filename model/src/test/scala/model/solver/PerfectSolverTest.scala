package model.solver

import model.BoardExamples.{easyExample, hardExample, mediumExample}
import model.SolverResult.MultipleSolutions
import model.{Dimensions, SudokuBoard, Validate}
import org.scalatest.funsuite.AnyFunSuite

class PerfectSolverTest extends AnyFunSuite {
  test("example 2x2") {
    val Some(board) = SudokuBoard.fromString(Dimensions(2, 2)) {
      """_ 2 4 _
        |1 _ _ 3
        |4 _ _ 2
        |_ 1 3 _
        |""".stripMargin
    }

    val Some(solution) = PerfectSolver(board).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("example 2x3") {
    val Some(board) = SudokuBoard.fromString(Dimensions(2, 3)) {
      """5 _ 1 _ 3 _
        |_ 1 4 3 _ _
        |3 _ _ _ _ _
        |_ 3 _ 2 1 5
        |_ 6 _ _ _ 3
        |1 _ 3 _ _ _
        |""".stripMargin
    }

    val Some(solution) = PerfectSolver(board).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("example 3x2") {
    val Some(board) = SudokuBoard.fromString(Dimensions(3, 2)) {
      """1 _ 3 _ 4 _
        |_ 6 _ 2 _ 3
        |6 4 _ 3 _ 1
        |3 _ 2 4 6 _
        |2 _ 1 _ 5 _
        |_ 5 _ 1 _ 2
        |""".stripMargin
    }

    val Some(solution) = PerfectSolver(board).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("easy example 3x3 can be solved") {
    val Some(solution) = PerfectSolver(easyExample).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("medium example 3x3 can be solved") {
    val Some(solution) = PerfectSolver(mediumExample).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("hard example 3x3 can be solved") {
    val Some(solution) = PerfectSolver(hardExample).uniqueSolution
    assert(Validate.correct(solution))
  }

  test("given an empty board") {
    val result = PerfectSolver(SudokuBoard.empty(Dimensions(3, 3)))
    assert(result.isInstanceOf[MultipleSolutions])
  }
}
