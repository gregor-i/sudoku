package model

import org.scalatest.funsuite.AnyFunSuite

class ValidateTest extends AnyFunSuite {
  test("noError checks for rule violations") {
    val dim   = Dimensions(2, 2)
    val board = SudokuBoard.empty(dim).set(0, 0, Some(1))
    assert(Validate.noError(board))
    assert(!Validate.noError(board.set(0, 1, Some(1))))
    assert(!Validate.noError(board.set(1, 0, Some(1))))
    assert(!Validate.noError(board.set(1, 1, Some(1))))
    assert(Validate.noError(board.set(0, 1, Some(2))))
  }

  test("apply constructs a SudokuBoard[Id] if the board is complete and correct") {
    val Some(completedBoard) = SudokuBoard.fromString(Dimensions(2, 2)) {
      """
        |1 2 3 4
        |3 4 1 2
        |2 3 4 1
        |4 1 2 3
        |""".stripMargin
    }

    assert(Validate.noError(completedBoard))
    assert(Validate(completedBoard).isDefined)
  }

  for (dim <- DimensionExamples.examples)
    test(s"empty boards are always valid (${dim})") {
      assert(Validate.noError(SudokuBoard.empty(dim)))
    }
}
