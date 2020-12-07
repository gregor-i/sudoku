package model

import model.solver.{FPSolver, IteratorSolver}
import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Failure, Success, Try}

class GeneratorTest extends AnyFunSuite {
  def generatorTest(seed: Int, solver: Solver) = {
    val dim = Dimensions(3, 3)

    test(s"apply constructs a puzzle (dim = ${dim}, seed = ${seed}, solver = ${solver})") {
      Generator(dim, seed, Difficulty.default)
    }

    Try {
      Generator(dim, seed, Difficulty.default)
    } match {
      case Failure(_) => ()
      case Success(board) =>
        test(s"the constructed board has a unique solution (dim = ${dim}, seed = ${seed}, solver = ${solver})") {
          assert(IteratorSolver(board).uniqueSolution.isDefined)
        }
    }
  }

  generatorTest(1, IteratorSolver)
  generatorTest(2, IteratorSolver)
  generatorTest(50, IteratorSolver)
  generatorTest(1564, IteratorSolver)
  generatorTest(-1564, IteratorSolver)

  generatorTest(1, FPSolver)
  generatorTest(2, FPSolver)
  generatorTest(50, FPSolver)
  generatorTest(1564, FPSolver)
  generatorTest(-1564, FPSolver)

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
