package model

object Validate {
  def apply(board: OpenSudokuBoard): Option[SolvedSudokuBoard] =
    if (board.data.forall(_.isDefined))
      Some(board.map(_.get)).filter(correct)
    else
      None

  def noError(board: OpenSudokuBoard): Boolean =
    SudokuBoard.allSubsets(board.dim).forall { positions =>
      val values = positions.flatMap(board.get)
      values == values.distinct
    }

  def correct(board: OpenSudokuBoard, pos: Position): Boolean =
    SudokuBoard.allSubsetsOf(pos)(board.dim).forall { subset =>
      val values = subset.flatMap(board.get)
      values == values.distinct
    }

  def correct(board: SolvedSudokuBoard): Boolean =
    SudokuBoard.allSubsets(board.dim).forall {
      _.map(board.get).sorted == SudokuBoard.values(board.dim)
    }
}
