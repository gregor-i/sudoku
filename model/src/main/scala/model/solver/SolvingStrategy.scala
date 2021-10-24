package model.solver

import model.SolverResult.{CouldNotSolve, UniqueSolution}
import model.{OpenSudokuBoard, Position, SolverResult, Subset, SudokuBoard, Validate}

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
}

private[solver] sealed trait SolvingStrategy {
  def solve(node: SolverNode): LazyList[(Position, Int)]
  def hint(board: SolverNode): Option[Hint]
}

private[solver] object SingleOptionForPosition extends SolvingStrategy {
  def solve(node: SolverNode): LazyList[(Position, Int)] =
    for {
      pos <- node.openPositions.to(LazyList)
      options = node.options.get(pos)
      if options.size == 1
      option <- options
    } yield (pos, option)

  def hint(node: SolverNode): Option[Hint] =
    solve(node).headOption.map {
      case (pos, value) =>
        NextInputHint(
          position = pos,
          value = value,
          blockingPositions =
            SudokuBoard.allSubsetsOf(pos)(node.board.dim).flatten.filter(pos => node.board.get(pos).isDefined).toSet
        )
    }
}

private[solver] object UniqueOptionInSubset extends SolvingStrategy {
  private def calculate(node: SolverNode): LazyList[(Position, Int, Subset)] =
    for {
      subset <- SudokuBoard.allSubsets(node.board.dim).to(LazyList)
      value  <- SudokuBoard.values(node.board.dim)
      posWithValue = subset.filter(node.options.get(_).contains(value))
      if posWithValue.size == 1
      pos <- posWithValue
    } yield (pos, value, subset)

  def solve(node: SolverNode): LazyList[(Position, Int)] =
    calculate(node).map { case (pos, value, _) => (pos, value) }

  def hint(node: SolverNode): Option[Hint] =
    calculate(node).headOption.map {
      case (pos, value, subset) =>
        NextInputHint(
          position = pos,
          value = value,
          blockingPositions = subset.toSet
        )
    }
}
