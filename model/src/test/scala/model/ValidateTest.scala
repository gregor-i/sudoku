package model

import model.BoardExamples._
import org.scalatest.funsuite.AnyFunSuite

class ValidateTest extends AnyFunSuite {
  test("noError checks for rule violations in the subsets of a single position") {
    val dim = Dimensions(2, 2)
    val Some(board) = SudokuBoard.fromString(dim)(
      """
        |1 4 2 _
        |3 2 _ 2
        |_ _ _ _
        |_ 4 _ _
        |""".stripMargin
    )

    val correctness = SudokuBoard(
      dim = dim,
      data = Vector(
        true, false, false, false, false, false, false, false, true, false, true, true, true, false, true, true
      )
    )

    for (pos <- SudokuBoard.positions(dim))
      assert(correctness.get(pos) == Validate.noError(board, pos))
  }

  test("apply returns a SolvedSudokuBoard if the board is complete and correct") {
    assert(Validate(completedBoard).isDefined)
  }
}
