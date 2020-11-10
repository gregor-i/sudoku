package model

object Validate {
  def apply(board: SudokuBoard[Option]): Option[SudokuBoard[Id]] =
    if (board.data.flatten.length == board.dim.blockSize * board.dim.blockSize)
      Some(new SudokuBoard[Id](board.dim, board.data.flatten))
        .filter(correct)
    else
      None

  def noError(board: SudokuBoard[Option]): Boolean =
    subsetNoError(board, SudokuBoard.columns(board.dim)) &&
      subsetNoError(board, SudokuBoard.rows(board.dim)) &&
      subsetNoError(board, SudokuBoard.blocks(board.dim))

  def correct(board: SudokuBoard[Id]): Boolean =
    subsetCompleted(board, SudokuBoard.columns(board.dim)) &&
      subsetCompleted(board, SudokuBoard.rows(board.dim)) &&
      subsetCompleted(board, SudokuBoard.blocks(board.dim))

  private def subsetNoError(board: SudokuBoard[Option], subset: Seq[Seq[(Int, Int)]]): Boolean =
    subset.forall { positions =>
      val values = positions.flatMap { case (x, y) => board.get(x, y) }
      values == values.distinct
    }

  private def subsetCompleted(board: SudokuBoard[Id], subset: Seq[Seq[(Int, Int)]]): Boolean =
    subset.forall {
      _.map { case (x, y) => board.get(x, y) }.sorted == SudokuBoard.values(board.dim)
    }
}
