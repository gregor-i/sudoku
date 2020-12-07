package model

import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Failure, Success, Try}

class GeneratorTest extends AnyFunSuite {
  def generatorTest(seed: Int) = {
    val dim = Dimensions(3, 3)

    test(s"apply constructs a puzzle (dim = ${dim}, seed = ${seed})") {
      Generator(dim, seed, Difficulty.default)
    }

    Try {
      Generator(dim, seed, Difficulty.default)
    } match {
      case Failure(_) => ()
      case Success(board) =>
        test(s"the constructed board has a unique solution (dim = ${dim}, seed = ${seed})") {
          assert(Solver.uniqueSolution(board).isDefined)
        }
    }
  }

  generatorTest(1)
  generatorTest(2)
  generatorTest(50)
  generatorTest(1564)
  generatorTest(-1564)

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
