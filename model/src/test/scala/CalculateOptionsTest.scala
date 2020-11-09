import org.scalatest.funsuite.AnyFunSuite

class CalculateOptionsTest extends AnyFunSuite {
  test("example1") {
    val dim   = Dimensions(2, 2)
    val pos   = (0, 0)
    val board = SudokuBoard.empty(dim).set(0, 0, Some(1))
    assert(CalculateOptions(board, pos) == Seq.empty)
    for (p <- SudokuBoard.blockOf(pos)(dim).filter(_ != pos))
      assert(CalculateOptions(board, p) == dim.values.filter(_ != 1))
    for (p <- SudokuBoard.rowOf(pos)(dim).filter(_ != pos))
      assert(CalculateOptions(board, p) == dim.values.filter(_ != 1))
    for (p <- SudokuBoard.columnOf(pos)(dim).filter(_ != pos))
      assert(CalculateOptions(board, p) == dim.values.filter(_ != 1))
    assert(CalculateOptions(board, (2, 2)) == dim.values)

  }
}
