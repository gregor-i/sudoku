package model

import org.scalatest.funsuite.AnyFunSuite

class GeneratorTest extends AnyFunSuite {
  for {
    difficulty <- Seq(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)
    dim        <- DimensionExamples.examples
    seed       <- Seq(1, 2, 50, 1564)
  } test(s"apply constructs a puzzle (dim = ${dim}), seed = ${seed}, difficulty = ${difficulty}) with an unique solution") {
    val puzzle = Generator(dim, seed, difficulty)

    assert(puzzle.data.count(_.isDefined) < dim.boardSize)

    assert(Solver.perfectSolver.apply(puzzle).uniqueSolution.isDefined)
  }

  test(s"initialBoard constructs a solved sudoku board") {
    for (dim <- DimensionExamples.examples) {
      val board = Generator.initialBoard(dim)
      assert(Validate.correct(board))
    }
  }

  test("permute swaps rows and columns, but always preserves correctness") {
    val dim     = Dimensions(2, 3)
    val initial = Generator.initialBoard(dim)

    for (seed <- 0 until 1000) {
      val permuted = Generator.permute(seed, initial)
      assert(Validate.correct(permuted))
    }
  }

  test("shuffleValues exchanged the values, but always preserves corretness") {
    val dim     = Dimensions(2, 3)
    val initial = Generator.initialBoard(dim)

    for (seed <- 0 until 1000) {
      val permuted = Generator.shuffleValues(seed, initial)
      assert(Validate.correct(permuted))
    }
  }
}
