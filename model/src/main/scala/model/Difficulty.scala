package model

import scala.annotation.tailrec
import scala.util.chaining.scalaUtilChainingOps

object Difficulty {
  def default = 1.99

  def apply(puzzle: OpenSudokuBoard): Option[Double] =
    Solver.solver(puzzle).uniqueSolution.map(solution => apply(puzzle, solution))

  def apply(puzzle: OpenSudokuBoard, solution: SolvedSudokuBoard): Double = {
    require(puzzle.dim == solution.dim)
    require(
      SudokuBoard
        .positions(puzzle.dim)
        .forall(pos => puzzle.get(pos).forall(_ == solution.get(pos)))
    )
    require(Validate.correct(solution))

    sequenceOfOptionSizes(puzzle, solution)
      .pipe(calculateDifficultyScore)
  }

  private def calculateDifficultyScore(seq: List[Int]): Double = {
    if (seq.isEmpty)
      0.0
    else
      seq.max + sigmoid(seq.sum * 0.1) / (0.5 * Math.PI)
  }

  private def sigmoid(x: Double): Double =
    1.0 / (1.0 + math.exp(-x))

  private def sequenceOfOptionSizes(puzzle: OpenSudokuBoard, solution: SolvedSudokuBoard): List[Int] = {
    @tailrec
    def loop(puzzle: OpenSudokuBoard, options: OptionsSudokuBoard, openPositions: Set[Position], acc: List[Int]): List[Int] =
      if (openPositions.isEmpty)
        acc
      else {
        val pos = openPositions.minBy(options.get(_).size)
        loop(
          puzzle = puzzle.set(pos, Some(solution.get(pos))),
          options = OptionsSudokuBoard.set(options, pos, solution.get(pos)),
          openPositions = openPositions - pos,
          acc = options.get(pos).size :: acc
        )
      }

    loop(
      puzzle = puzzle,
      options = OptionsSudokuBoard(puzzle),
      openPositions = SudokuBoard.positions(puzzle.dim).filter(puzzle.get(_).isEmpty).toSet,
      acc = Nil
    )
  }
}
