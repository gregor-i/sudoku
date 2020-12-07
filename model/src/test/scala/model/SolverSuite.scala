package model

import model.BoardExamples.hardExample
import org.scalatest.funsuite.AnyFunSuite

trait SolverSuite {
  _: AnyFunSuite =>
  def testSolver(solver: Solver): Unit = {
    test("example 2x2") {
      val Some(board) = SudokuBoard.fromString(Dimensions(2, 2)) {
        """_ 2 4 _
          |1 _ _ 3
          |4 _ _ 2
          |_ 1 3 _
          |""".stripMargin
      }

      val Some(solution) = solver(board).uniqueSolution
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

      val Some(solution) = solver(board).uniqueSolution
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

      val Some(solution) = solver(board).uniqueSolution
      assert(Validate.correct(solution))
    }

    test("hard example 3x3") {
      val Some(solution) = solver(hardExample).uniqueSolution
      assert(Validate.correct(solution))
    }
  }
}
