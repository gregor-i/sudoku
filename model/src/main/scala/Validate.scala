object Validate {
  def option(board: SudokuBoard[Option]): Boolean =
    validateSubsetOption(board, SudokuBoard.columns(board.dim)) &&
      validateSubsetOption(board, SudokuBoard.rows(board.dim)) &&
      validateSubsetOption(board, SudokuBoard.blocks(board.dim))

  def some(board: SudokuBoard[Some]): Boolean =
    validateSubsetSome(board, SudokuBoard.columns(board.dim)) &&
      validateSubsetSome(board, SudokuBoard.rows(board.dim)) &&
      validateSubsetSome(board, SudokuBoard.blocks(board.dim))

  private def validateSubsetOption(board: SudokuBoard[Option], positions: Seq[Seq[(Int, Int)]]): Boolean =
    positions.forall {
      _.flatMap { case (x, y) => board.get(x, y) }
        .groupBy(identity)
        .forall(_._2.length == 1)
    }

  private def validateSubsetSome(board: SudokuBoard[Some], positions: Seq[Seq[(Int, Int)]]): Boolean =
    positions.forall {
      _.flatMap { case (x, y) => board.get(x, y) }.toSet == board.dim.values.toSet
    }
}
