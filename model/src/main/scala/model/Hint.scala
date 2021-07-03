package model

case class Hint(
    position: Position,
    value: Int,
    blockingPositions: Set[Position]
)

object Hint {

  def of(board: OpenSudokuBoard): Option[Hint] = singleOptionForPosition(board)

  private def singleOptionForPosition(board: OpenSudokuBoard): Option[Hint] = {
    val options = OptionsSudokuBoard(board)
    (for {
      pos <- SudokuBoard.positions(board.dim).to(LazyList)
      optionsAtPos = options.get(pos)
      if optionsAtPos.size == 1
      option <- optionsAtPos
    } yield Hint(
      position = pos,
      value = option,
      blockingPositions = SudokuBoard.allSubsetsOf(pos)(board.dim).flatten.filter(pos => board.get(pos).isDefined).toSet
    )).headOption
  }
}
