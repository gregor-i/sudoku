package model.infinite

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

    val inputForGenerator: SudokuBoard[PuzzleCell.Empty | PuzzleCell.Given] =
      merge(
        puzzle.map {
          case cell: PuzzleCell.Given => cell
          case cell                   => PuzzleCell.Empty(cell.solution)
        },
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

  private def merge[A, B](left: SudokuBoard[A], right: SudokuBoard[B], positions: Seq[Position]): SudokuBoard[A | B] = {
    require(left.dim == right.dim)
    SudokuBoard.fill[A | B](left.dim) {
      pos =>
        if (positions.contains(pos))
          right.get(pos)
        else
          left.get(pos)
    }
  }
}
