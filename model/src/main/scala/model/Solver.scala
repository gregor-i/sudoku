package model

object Solver {

  def apply(board: OpenSudokuBoard): LazyList[SolvedSudokuBoard] =
    if (Validate.noError(board))
      loop(board)
    else
      LazyList.empty

  private def loop(board: OpenSudokuBoard): LazyList[SolvedSudokuBoard] =
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
