package model.inifinit

import model.*

import scala.annotation.tailrec
import scala.util.Random
import scala.util.chaining.*

object ContinuePuzzle {
  def maybeContinue(puzzle: DecoratedBoard, seed: Int, difficulty: Difficulty): Option[DecoratedBoard] = {
    val solution = Solver
      .perfectSolver(puzzle.map {
        case DecoratedCell.Given(value) => Some(value)
        case _                          => None
      })
      .uniqueSolution
      .get
    def subsetIsCompleted(subset: Subset): Boolean = subset.forall(pos => puzzle.get(pos).toOption.contains(solution.get(pos)))

    val completedRowsAndColumns = SudokuBoard
      .columnBlocks(puzzle.dim)
      .concat(SudokuBoard.rowBlocks(puzzle.dim))
      .filter(subsetIsCompleted)
      .flatten
      .distinct

    if (completedRowsAndColumns.nonEmpty)
      Some(apply(solution, puzzle, completedRowsAndColumns, seed, difficulty))
    else
      None
  }

  def apply(
      solution: SolvedSudokuBoard,
      puzzle: DecoratedBoard,
      positions: Seq[Position],
      seed: Int,
      difficulty: Difficulty
  ): DecoratedBoard = {
    require(solution.dim == puzzle.dim)
    require(positions.forall(pos => puzzle.get(pos).toOption.contains(solution.get(pos))))

    val random = Random(seed)

    val alternativeSolution = ContinuationOptions(solution, positions.toSet, seed = random.nextInt()).head

    val inputForGenerator =
      merge(
        puzzle.map {
          case DecoratedCell.Given(value) => Some(value)
          case _                          => None
        },
        alternativeSolution.map[Option[Int]](Some.apply),
        positions
      )

    val continuedPuzzle =
      Generator
        .makePuzzle(
          random = random,
          positions = random.shuffle(positions),
          board = inputForGenerator,
          solver = Solver.forDifficulty(difficulty)
        )
        .map {
          case Some(value) => DecoratedCell.Given(value)
          case None        => DecoratedCell.Empty
        }

    merge(puzzle, continuedPuzzle, positions)
  }

  private def merge[A](left: SudokuBoard[A], right: SudokuBoard[A], positions: Seq[Position]): SudokuBoard[A] = {
    require(left.dim == right.dim)
    SudokuBoard.fill[A](left.dim) {
      pos =>
        if (positions.contains(pos))
          right.get(pos)
        else
          left.get(pos)
    }
  }
}
