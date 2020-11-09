import org.scalatest.funsuite.AnyFunSuite

class ValidateTest extends AnyFunSuite {
  test("option1") {
    val dim   = Dimensions(2, 2)
    val board = SudokuBoard.empty(dim).set(0, 0, Some(1))
    assert(Validate.option(board))
    assert(!Validate.option(board.set(0, 1, Some(1))))
    assert(!Validate.option(board.set(1, 0, Some(1))))
    assert(!Validate.option(board.set(1, 1, Some(1))))
    assert(Validate.option(board.set(0, 1, Some(2))))
  }

  for (dim <- DimensionExamples.examples)
    test(s"empty boards are always valid (${dim})") {
      assert(Validate.option(SudokuBoard.empty(dim)))
    }
}
