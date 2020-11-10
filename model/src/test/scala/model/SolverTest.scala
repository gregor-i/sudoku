package model

import org.scalatest.funsuite.AnyFunSuite

class SolverTest extends AnyFunSuite {
  test("example 2x2") {
    val Some(board) = SudokuBoard.fromString(Dimensions(2, 2)) {
      """_ 2 4 _
        |1 _ _ 3
        |4 _ _ 2
        |_ 1 3 _
        |""".stripMargin
    }

    val solved = Solver(board)
    assert(solved.length == 1)
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

    val solved = Solver(board)
    assert(solved.length == 1)
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

    val solved = Solver(board)
    assert(solved.length == 1)
  }

  test("hard example 3x3") {
    val Some(board) = SudokuBoard.fromString(Dimensions(3, 3)) {
      """
        |8 _ _ _ _ _ _ _ _
        |_ _ 3 6 _ _ _ _ _
        |_ 7 _ _ 9 _ 2 _ _
        |_ 5 _ _ _ 7 _ _ _
        |_ _ _ _ 4 5 7 _ _
        |_ _ _ 1 _ _ _ 3 _
        |_ _ 1 _ _ _ _ 6 8
        |_ _ 8 5 _ _ _ 1 _
        |_ 9 _ _ _ _ 4 _ _
        |""".stripMargin
    }

    val solved = Solver(board)
    assert(solved.length == 1)
  }
}
