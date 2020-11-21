package model

import model.BoardExamples._
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
    assert(Validate.noError(completedBoard))
    assert(Validate.findErrors(completedBoard) == Set.empty)
    assert(Validate(completedBoard).isDefined)
  }

  test("findErrors finds positions with errors") {
    assert(!Validate.noError(boardErrorBlock))
    assert(Validate.findErrors(boardErrorBlock) == Set((0, 0), (1, 1)))

    assert(!Validate.noError(boardErrorRow))
    assert(Validate.findErrors(boardErrorRow) == Set((0, 0), (3, 0)))

    assert(!Validate.noError(boardErrorColumn))
    assert(Validate.findErrors(boardErrorColumn) == Set((0, 0), (0, 2)))
  }

  test(s"empty boards are always valid") {
    for (dim <- DimensionExamples.examples)
      assert(Validate.noError(SudokuBoard.empty(dim)))
  }
}
