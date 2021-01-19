package model

import model.solver.{IteratorSolver, RecursionSolver, TreeDFSSolver}
import org.scalatest.funsuite.AnyFunSuite

import scala.util.{Failure, Success, Try}

class GeneratorTest extends AnyFunSuite {
  def generatorTest(seed: Int, solver: Solver) = {
    val dim = Dimensions(3, 3)

    test(s"${solver.getClass.getSimpleName}: apply constructs a puzzle (dim = ${dim}), seed = ${seed})") {
      Generator(dim, seed, Difficulty.medium)
    }

    Try {
      Generator(dim, seed, Difficulty.medium)
    } match {
      case Failure(_) => ()
      case Success(board) =>
        test(s"${solver.getClass.getSimpleName}: the constructed board has a unique solution (dim = ${dim}, seed = ${seed})") {
          assert(IteratorSolver(board).uniqueSolution.isDefined)
        }
    }
  }

  generatorTest(1, IteratorSolver)
  generatorTest(2, IteratorSolver)
  generatorTest(50, IteratorSolver)
  generatorTest(1564, IteratorSolver)
  generatorTest(-1564, IteratorSolver)

  generatorTest(1, RecursionSolver)
  generatorTest(2, RecursionSolver)
  generatorTest(50, RecursionSolver)
  generatorTest(1564, RecursionSolver)
  generatorTest(-1564, RecursionSolver)

  generatorTest(1, TreeDFSSolver)
  generatorTest(2, TreeDFSSolver)
  generatorTest(50, TreeDFSSolver)
  generatorTest(1564, TreeDFSSolver)
  generatorTest(-1564, TreeDFSSolver)

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
