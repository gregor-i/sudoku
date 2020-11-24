package model

object FPSolver {
  def apply(board: OpenSudokuBoard): LazyList[SolvedSudokuBoard] =
    if (Validate.noError(board))
      loop(
        board,
        OptionsSudokuBoard(board),
        SudokuBoard.positions(board.dim).filter(board.get(_).isEmpty).toList
      )
    else
      LazyList.empty

  private def loop(
      board: OpenSudokuBoard,
      optionsBoard: OptionsSudokuBoard,
      openPositions: List[Position]
  ): LazyList[SolvedSudokuBoard] = {
    openPositions.headOption match {
      case Some(pos) =>
        for {
          option <- optionsBoard.get(pos).to(LazyList)
          child <- loop(
            board.set(pos, Some(option)),
            OptionsSudokuBoard.set(optionsBoard, pos, option),
            openPositions.tail
          )
        } yield child
      case None =>
        Validate(board).to(LazyList)
    }
  }
}
