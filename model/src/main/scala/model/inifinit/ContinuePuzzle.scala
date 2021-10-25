package model.inifinit

import model.*

import scala.annotation.tailrec
import scala.util.Random
import scala.util.chaining.*

object ContinuePuzzle {
  def maybeContinue(puzzle: SudokuPuzzle, seed: Int, difficulty: Difficulty): Option[SudokuPuzzle] = {
    def subsetIsCompleted(subset: Subset): Boolean = subset.forall(pos => puzzle.get(pos).isCorrectAndFilled)

    val completedRowsAndColumns = SudokuBoard
      .columnBlocks(puzzle.dim)
      .concat(SudokuBoard.rowBlocks(puzzle.dim))
      .filter(subsetIsCompleted)
      .flatten
      .distinct

    if (completedRowsAndColumns.nonEmpty)
      Some(apply(puzzle, completedRowsAndColumns, seed, difficulty))
    else
      None
  }

  def apply(
      puzzle: SudokuPuzzle,
      positions: Seq[Position],
      seed: Int,
      difficulty: Difficulty
  ): SudokuPuzzle = {
    require(positions.forall(pos => puzzle.get(pos).isCorrectAndFilled))

    val random = Random(seed)

    val alternativeSolution = ContinuationOptions(puzzle.map(_.solution), positions.toSet, seed = random.nextInt()).head

    val inputForGenerator: SudokuPuzzle =
      merge(
        puzzle,
        alternativeSolution.map(PuzzleCell.Given.apply),
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
