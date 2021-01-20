package model

import model.solver.{IteratorSolver, RecursionSolver, TreeDFSSolver}
import org.scalatest.funsuite.AnyFunSuite

class GeneratorTest extends AnyFunSuite {
  def generatorTest(solver: Solver, dim: Dimensions = Dimensions(3, 3)) =
    for (seed <- Seq(1, 2, 50, 1564, -1564))
      test(s"${solver.getClass.getSimpleName}: apply constructs a puzzle (dim = ${dim}), seed = ${seed})") {
        Generator(dim, seed, Difficulty.medium)
      }

  generatorTest(IteratorSolver)
  generatorTest(RecursionSolver)
  generatorTest(TreeDFSSolver)

  test(s"initialBoard") {
    for (dim <- DimensionExamples.examples) {
      val board = Generator.initialBoard(dim)
      assert(Validate.correct(board))
    }
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
