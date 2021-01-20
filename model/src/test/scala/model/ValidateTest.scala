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

  test("apply returns a SolvedSudokuBoard if the board is complete and correct") {
    assert(Validate.noError(completedBoard))
    assert(Validate(completedBoard).isDefined)
  }

  test("findErrors finds positions with errors") {
    assert(!Validate.noError(boardErrorBlock))
    assert(!Validate.noError(boardErrorRow))
    assert(!Validate.noError(boardErrorColumn))
  }

  test(s"empty boards are always valid") {
    for (dim <- DimensionExamples.examples)
      assert(Validate.noError(SudokuBoard.empty(dim)))
  }
}
