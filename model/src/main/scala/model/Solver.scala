package model

object Solver {

  def apply(board: SudokuBoard[Option]): LazyList[SudokuBoard[Id]] =
    if (Validate.noError(board))
      loop(board)
    else
      LazyList.empty

  private def loop(board: SudokuBoard[Option]): LazyList[SudokuBoard[Id]] =
    SudokuBoard
      .positions(board.dim)
      .filter(board.get(_).isEmpty)
      .map(p => (p, CalculateOptions(board, p)))
      .minByOption(_._2.size) match {
      case None             => Validate(board).to(LazyList)
      case Some((_, Seq())) => LazyList.empty
      case Some((pos, options)) =>
        for {
          option <- options.to(LazyList)
          child  <- apply(board.set(pos, Some(option)))
        } yield child
    }
}
