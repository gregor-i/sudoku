package model.solver

import model.SolverResult.{CouldNotSolve, UniqueSolution}
import model._

import scala.annotation.tailrec

object EasySolver extends Solver {
  def apply(puzzle: OpenSudokuBoard): SolverResult = {
    @tailrec
    def loop(node: SolverNode): SolverResult = {
      if (node.openPositions.isEmpty) {
        Validate(node.board).fold[SolverResult](CouldNotSolve)(UniqueSolution.apply)
      } else {
        val solvedPositions = singleOptionForPosition(node)

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

  private[solver] def singleOptionForPosition(node: SolverNode): Iterable[(Position, Int)] =
    for {
      pos <- node.openPositions
      if node.options.get(pos).size == 1
      option <- node.options.get(pos)
    } yield (pos, option)
}
