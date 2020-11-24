package model

import scala.util.chaining._

object OptionsSudokuBoard {
  def apply(board: OpenSudokuBoard): OptionsSudokuBoard =
    SudokuBoard
      .positions(board.dim)
      .flatMap(pos => board.get(pos).map(value => (pos, value)))
      .foldLeft(initial(board.dim)) {
        case (options, (pos, value)) => set(options, pos, value)
      }

  def initial(dim: Dimensions): OptionsSudokuBoard = {
    val values = SudokuBoard.values(dim).toSet
    SudokuBoard(dim, Vector.fill(dim.boardSize)(values))
  }

  def set(board: OptionsSudokuBoard, pos: Position, value: Int): OptionsSudokuBoard =
    board
      .set(pos, Set.empty)
      .pipe(b => SudokuBoard.rowOf(pos)(board.dim).foldLeft(b)((board, pos) => board.mod(pos, _ - value)))
      .pipe(b => SudokuBoard.columnOf(pos)(board.dim).foldLeft(b)((board, pos) => board.mod(pos, _ - value)))
      .pipe(b => SudokuBoard.blockOf(pos)(board.dim).foldLeft(b)((board, pos) => board.mod(pos, _ - value)))
}
