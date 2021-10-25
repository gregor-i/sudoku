package model.infinite

import model.solver.PerfectSolver
import model.{Difficulty, Dimensions, Generator, SudokuBoard}
import org.scalatest.funsuite.AnyFunSuite

class ContinuationOptionsTest extends AnyFunSuite {
  test("ContinuationOptions.apply") {
    val dim   = Dimensions(3, 3)
    val board = Generator(dim, 0, Difficulty.Easy).map(_.solution)
    val area  = SudokuBoard.columnBlock(1, dim)

    val options = ContinuationOptions.apply(board, area = area.toSet, seed = 5)

    // a lot of boards will continue the given board
    assert(options.length > 100)

    // the positions outside of the specified area, are unchanged
    for {
      option <- options
      pos    <- SudokuBoard.positions(dim)
      if !area.contains(pos)
    } assert(option.get(pos) == board.get(pos))

    // only a single option is identical to the given board
    assert(options.count(_ == board) == 1)

    // all options are different
    assert(options.toSet.size == options.size)
  }
}
