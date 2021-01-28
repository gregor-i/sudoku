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

  def noError(board: OpenSudokuBoard, pos: Position): Boolean =
    board.get(pos).forall { value =>
      SudokuBoard
        .allSubsetsOf(pos)(board.dim)
        .forall(
          _.flatMap(board.get)
            .count(_ == value) <= 1
        )
    }

  def correct(board: SolvedSudokuBoard): Boolean =
    SudokuBoard.allSubsets(board.dim).forall {
      _.map(board.get).sorted == SudokuBoard.values(board.dim)
    }
}
