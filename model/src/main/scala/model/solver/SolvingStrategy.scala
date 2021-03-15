package model.solver

import model.SolverResult.{CouldNotSolve, UniqueSolution}
import model.{OpenSudokuBoard, Position, SolverResult, SudokuBoard, Validate}

import scala.annotation.tailrec

private[solver] object SolvingStrategy {
  def solveWithStrategy(puzzle: OpenSudokuBoard)(strategy: SolverNode => Iterable[(Position, Int)]): SolverResult = {
    @tailrec
    def loop(node: SolverNode): SolverResult = {
      if (node.openPositions.isEmpty) {
        Validate(node.board).fold[SolverResult](CouldNotSolve)(UniqueSolution.apply)
      } else {
        val solvedPositions = strategy(node)

        if (solvedPositions.isEmpty)
          CouldNotSolve
        else
          loop {
            solvedPositions
              .foldLeft(node) { case (node, (pos, value)) => SolverNode.setValue(node, pos, value) }
          }
      }
    }

    loop(SolverNode.initial(puzzle))
  }

  def singleOptionForPosition(node: SolverNode): Iterable[(Position, Int)] =
    for {
      pos <- node.openPositions
      options = node.options.get(pos)
      if options.size == 1
      option <- options
    } yield (pos, option)

  def uniqueOptionInSubset(node: SolverNode): Iterable[(Position, Int)] =
    for {
      subset <- SudokuBoard.allSubsets(node.board.dim)
      value  <- SudokuBoard.values(node.board.dim)
      posWithValue = subset.filter(node.options.get(_).contains(value))
      if posWithValue.size == 1
      pos <- posWithValue
    } yield (pos, value)
}
