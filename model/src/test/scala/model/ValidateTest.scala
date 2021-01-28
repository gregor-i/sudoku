package model

import model.BoardExamples._
import org.scalatest.funsuite.AnyFunSuite

class ValidateTest extends AnyFunSuite {
  test("noError checks for rule violations over the whole board") {
    val Some(board) = SudokuBoard.fromString(Dimensions(2, 2))(
      """
        |1 _ _ _
        |_ _ _ _
        |_ _ _ _
        |_ _ _ _
        |""".stripMargin
    )
    assert(Validate.noError(board))
    assert(!Validate.noError(board.set(0, 1, Some(1))))
    assert(!Validate.noError(board.set(1, 0, Some(1))))
    assert(!Validate.noError(board.set(1, 1, Some(1))))
    assert(Validate.noError(board.set(0, 1, Some(2))))
  }

  test("noError checks for rule violations for a single position") {
    val Some(board) = SudokuBoard.fromString(Dimensions(2, 2))(
      """
        |1 4 2 _
        |3 2 _ 2
        |_ _ _ _
        |_ 4 _ _
        |""".stripMargin
    )

    assert(Validate.noError(board, (0, 0)))
    assert(Validate.noError(board, (0, 1)))
    assert(Validate.noError(board, (0, 2)))
    assert(Validate.noError(board, (0, 3)))

    assert(!Validate.noError(board, (1, 0)))
    assert(!Validate.noError(board, (1, 1)))
    assert(Validate.noError(board, (1, 2)))
    assert(!Validate.noError(board, (1, 3)))

    assert(!Validate.noError(board, (2, 0)))
    assert(Validate.noError(board, (2, 1)))
    assert(Validate.noError(board, (2, 2)))
    assert(Validate.noError(board, (2, 3)))

    assert(Validate.noError(board, (3, 0)))
    assert(!Validate.noError(board, (3, 1)))
    assert(Validate.noError(board, (3, 2)))
    assert(Validate.noError(board, (3, 3)))
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
