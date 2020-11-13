package model

object CalculateOptions {
  def apply(board: OpenSudokuBoard, position: (Int, Int)): Seq[Int] = {
    board.get(position._1, position._2) match {
      case Some(_) => Seq.empty
      case None =>
        val context = SudokuBoard.rowOf(position)(board.dim) ++
          SudokuBoard.columnOf(position)(board.dim) ++
          SudokuBoard.blockOf(position)(board.dim)
        val usedValues = context.flatMap { case (x, y) => board.get(x, y) }.toSet
        SudokuBoard.values(board.dim).filter(!usedValues(_))
    }
  }
}
