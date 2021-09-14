package model

object OptionsSudokuBoard {
  def apply(board: OpenSudokuBoard): OptionsSudokuBoard =
    SudokuBoard.fill(board.dim) {
      pos =>
        if (board.get(pos).isDefined)
          Set.empty
        else
          SudokuBoard
            .values(board.dim)
            .toSet
            .removedAll(SudokuBoard.allSubsetsOf(pos)(board.dim).flatten.flatMap(board.get))
    }

  def initial(dim: Dimensions): OptionsSudokuBoard = {
    val values = SudokuBoard.values(dim).toSet
    SudokuBoard.fill(dim)(_ => values)
  }

  def set(board: OptionsSudokuBoard, pos: Position, value: Int): OptionsSudokuBoard =
    SudokuBoard
      .allSubsetsOf(pos)(board.dim)
      .flatten
      .toSet
      .foldLeft(board)((board, pos) => board.mod(pos, _ - value))
      .set(pos, Set.empty)
}
