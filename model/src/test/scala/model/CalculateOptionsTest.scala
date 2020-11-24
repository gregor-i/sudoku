package model

import model.BoardExamples.hardExample
import org.scalatest.funsuite.AnyFunSuite

class CalculateOptionsTest extends AnyFunSuite {
  val example1 = SudokuBoard
    .fromString(Dimensions(2, 2)) {
      """1 _ _ _
        |_ _ _ _
        |_ _ _ _
        |_ _ _ _
        |""".stripMargin
    }
    .get

  test("example1") {
    val pos   = (0, 0)
    val board = example1
    assert(CalculateOptions(board, pos) == Seq.empty)
    for (p <- SudokuBoard.blockOf(pos)(board.dim).filter(_ != pos))
      assert(CalculateOptions(board, p) == SudokuBoard.values(board.dim).filter(_ != 1))
    for (p <- SudokuBoard.rowOf(pos)(board.dim).filter(_ != pos))
      assert(CalculateOptions(board, p) == SudokuBoard.values(board.dim).filter(_ != 1))
    for (p <- SudokuBoard.columnOf(pos)(board.dim).filter(_ != pos))
      assert(CalculateOptions(board, p) == SudokuBoard.values(board.dim).filter(_ != 1))
    assert(CalculateOptions(board, (2, 2)) == SudokuBoard.values(board.dim))
  }

  def invariant(name: String, board: OpenSudokuBoard) =
    test(s"all options can be calculated at once ${name}") {
      val options = OptionsSudokuBoard(board)
      for (pos <- SudokuBoard.positions(board.dim))
        assert(options.get(pos) == CalculateOptions(board, pos).toSet)
    }

  invariant("example 1", example1)
  invariant("hardBoard", hardExample)
}
