package model

import model.solver.PerfectSolver
import org.scalatest.funsuite.AnyFunSuite

class GeneratorTest extends AnyFunSuite {
  test(s"apply constructs a puzzle with an unique solution") {
    for {
      difficulty <- Seq(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)
      dim        <- DimensionExamples.examples
      seed       <- Seq(1, 2, 50, 1564)
    } {
      val puzzle = Generator(dim, seed, difficulty)

      assert(puzzle.data.count(_.isGiven) < dim.boardSize)
      assert(puzzle.data.count(_.isInput) == 0)
      assert(PerfectSolver.uniqueSolution(puzzle.map(_.visible)).isDefined)
    }
  }
}
