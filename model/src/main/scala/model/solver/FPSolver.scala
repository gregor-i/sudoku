package model.solver

import model._

import scala.util.chaining.scalaUtilChainingOps

private[model] object FPSolver extends Solver {
  def apply(board: OpenSudokuBoard): SolverResult =
    if (Validate.noError(board))
      loop(
        board,
        OptionsSudokuBoard(board),
        SudokuBoard.positions(board.dim).filter(board.get(_).isEmpty).toSet
      ).pipe(SolverResult.fromLazyList)
    else
      SolverResult.NoSolution

  private def loop(
      board: OpenSudokuBoard,
      optionsBoard: OptionsSudokuBoard,
      openPositions: Set[Position]
  ): LazyList[SolvedSudokuBoard] = {
    openPositions.minByOption(optionsBoard.get(_).size) match {
      case Some(pos) =>
        for {
          option <- optionsBoard.get(pos).to(LazyList)
          child <- loop(
            board.set(pos, Some(option)),
            OptionsSudokuBoard.set(optionsBoard, pos, option),
            openPositions - pos
          )
        } yield child
      case None =>
        Validate(board).to(LazyList)
    }
  }
}
