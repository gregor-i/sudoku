package model.solver

import model.SolverResult.{CouldNotSolve, UniqueSolution}
import model._

import scala.annotation.tailrec

object MediumSolver extends Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult = {
    @tailrec
    def loop(node: SolverNode): SolverResult = {
      if (node.openPositions.isEmpty) {
        Validate(node.board).fold[SolverResult](CouldNotSolve)(UniqueSolution.apply)
      } else {
        val solvedPositions = EasySolver.singleOptionForPosition(node) ++ uniqueOptionInSubset(node)

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

  def uniqueOptionInSubset(node: SolverNode): Iterable[(Position, Int)] =
    for {
      subset <- SudokuBoard.allSubsets(node.board.dim)
      value  <- SudokuBoard.values(node.board.dim)
      if subset.count(node.options.get(_).contains(value)) == 1
      pos <- subset.filter(node.options.get(_).contains(value))
    } yield (pos, value)
}
