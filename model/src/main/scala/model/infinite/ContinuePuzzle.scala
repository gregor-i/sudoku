package model.infinite

import model.*

import scala.annotation.tailrec
import scala.util.Random
import scala.util.chaining.*

object ContinuePuzzle {
  def maybeContinue(puzzle: SudokuPuzzle, seed: Int, difficulty: Difficulty): Option[SudokuPuzzle] = {
    val positions = SudokuBoard
      .columnBlocks(puzzle.dim)
      .concat(SudokuBoard.rowBlocks(puzzle.dim))
      .filter(_.forall(pos => puzzle.get(pos).isCorrectAndFilled))
      .flatten
      .distinct

    if (positions.nonEmpty) {
      val random = Random(seed)

      val alternativeSolution = ContinuationOptions(puzzle.map(_.solution), positions.toSet, seed = random.nextInt()).head

      val inputForGenerator =
        merge(
          puzzle.map[PuzzleCell.Given | PuzzleCell.Empty] {
            case cell: PuzzleCell.Given => cell
            case cell                   => PuzzleCell.Empty(cell.solution)
          },
          alternativeSolution.map(PuzzleCell.Given.apply),
          positions
        )

      val continuedPuzzle =
        Generator
          .makePuzzle(
            positions = random.shuffle(positions).map(List(_)),
            board = inputForGenerator,
            solver = Solver.forDifficulty(difficulty)
          )

      Some(merge(puzzle, continuedPuzzle, positions))
    } else None
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
