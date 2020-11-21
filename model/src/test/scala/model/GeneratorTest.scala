package model

import org.scalatest.funsuite.AnyFunSuite

class GeneratorTest extends AnyFunSuite {
  test("apply constructs a puzzle with a unique solution") {
    val dim   = Dimensions(3, 3)
    val board = Generator(dim, 1)
    assert(Solver(board).length == 1)
  }

  test("initialBoard") {
    val dim = Dimensions(3, 3)
    Generator.initialBoard(dim)
  }

  test("permutate swaps rows and columns, but always preserves correctness") {
    val dim     = Dimensions(2, 3)
    val initial = Generator.initialBoard(dim)

    for (seed <- 0 until 1000) {
      val permutated = Generator.permutate(seed, initial)
      assert(Validate.correct(permutated))
    }
  }
}
