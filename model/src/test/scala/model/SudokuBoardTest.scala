package model

import org.scalatest.funsuite.AnyFunSuite

class SudokuBoardTest extends AnyFunSuite {
  test("construct an empty board") {
    val board = SudokuBoard.empty(Dimensions(3, 2))
    for {
      x <- 0 until 6
      y <- 0 until 6
    } assert(board.get(x, y) == None)
  }

  test("set a value") {
    val board = SudokuBoard.empty(Dimensions(3, 2)).set(5, 1, Some(5))
    assert(board.get(5, 1) == Some(5))
    assert(board.get(5, 0) == None)
    assert(board.get(5, 2) == None)

  }

  test("model.SudokuBoard.row(i)") {
    assert(SudokuBoard.row(0)(Dimensions(2, 2)) == Seq((0, 0), (1, 0), (2, 0), (3, 0)))
    assert(SudokuBoard.row(1)(Dimensions(2, 2)) == Seq((0, 1), (1, 1), (2, 1), (3, 1)))
    assert(SudokuBoard.row(2)(Dimensions(2, 2)) == Seq((0, 2), (1, 2), (2, 2), (3, 2)))
    assert(SudokuBoard.row(3)(Dimensions(2, 2)) == Seq((0, 3), (1, 3), (2, 3), (3, 3)))
  }

  test("model.SudokuBoard.column(i)") {
    assert(SudokuBoard.column(0)(Dimensions(2, 2)) == Seq((0, 0), (0, 1), (0, 2), (0, 3)))
    assert(SudokuBoard.column(1)(Dimensions(2, 2)) == Seq((1, 0), (1, 1), (1, 2), (1, 3)))
    assert(SudokuBoard.column(2)(Dimensions(2, 2)) == Seq((2, 0), (2, 1), (2, 2), (2, 3)))
    assert(SudokuBoard.column(3)(Dimensions(2, 2)) == Seq((3, 0), (3, 1), (3, 2), (3, 3)))
  }

  test("model.SudokuBoard.block(i)") {
    assert(SudokuBoard.block(0)(Dimensions(2, 2)) == Seq((0, 0), (1, 0), (0, 1), (1, 1)))
    assert(SudokuBoard.block(1)(Dimensions(2, 2)) == Seq((2, 0), (3, 0), (2, 1), (3, 1)))
    assert(SudokuBoard.block(2)(Dimensions(2, 2)) == Seq((0, 2), (1, 2), (0, 3), (1, 3)))
    assert(SudokuBoard.block(3)(Dimensions(2, 2)) == Seq((2, 2), (3, 2), (2, 3), (3, 3)))
  }

  test("model.SudokuBoard.rows") {
    val dim = Dimensions(2, 2)
    assert(
      SudokuBoard.rows(dim) == Seq(
        SudokuBoard.row(0)(dim),
        SudokuBoard.row(1)(dim),
        SudokuBoard.row(2)(dim),
        SudokuBoard.row(3)(dim)
      )
    )
  }

  test("model.SudokuBoard.columns") {
    val dim = Dimensions(2, 2)
    assert(
      SudokuBoard.columns(dim) == Seq(
        SudokuBoard.column(0)(dim),
        SudokuBoard.column(1)(dim),
        SudokuBoard.column(2)(dim),
        SudokuBoard.column(3)(dim)
      )
    )
  }

  test("model.SudokuBoard.blocks") {
    val dim = Dimensions(2, 2)
    assert(
      SudokuBoard.blocks(dim) == Seq(
        SudokuBoard.block(0)(dim),
        SudokuBoard.block(1)(dim),
        SudokuBoard.block(2)(dim),
        SudokuBoard.block(3)(dim)
      )
    )
  }

  for (dim <- DimensionExamples.examples)
    test(s"each position is in block, row and column (${dim})") {
      assert(SudokuBoard.rows(dim).flatten.toSet == SudokuBoard.positions(dim).toSet)
      assert(SudokuBoard.columns(dim).flatten.toSet == SudokuBoard.positions(dim).toSet)
      assert(SudokuBoard.blocks(dim).flatten.toSet == SudokuBoard.positions(dim).toSet)
    }

  for (dim <- DimensionExamples.examples)
    test(s"rowOf (${dim})") {
      for {
        row <- SudokuBoard.rows(dim)
        pos <- row
      } {
        assert(SudokuBoard.rowOf(pos)(dim) == row)
      }
    }

  for (dim <- DimensionExamples.examples)
    test(s"columnOf (${dim})") {
      for {
        column <- SudokuBoard.columns(dim)
        pos    <- column
      } {
        assert(SudokuBoard.columnOf(pos)(dim) == column)
      }
    }

  for (dim <- DimensionExamples.examples)
    test(s"blockOf (${dim})") {
      for {
        block <- SudokuBoard.blocks(dim)
        pos   <- block
      } {
        assert(SudokuBoard.blockOf(pos)(dim) == block)
      }
    }

  test("fill") {
    val positionsBoard = SudokuBoard.fill(Dimensions(3, 3))(identity)
    for (position <- SudokuBoard.positions(positionsBoard.dim))
      assert(positionsBoard.get(position) == position)
  }
}
