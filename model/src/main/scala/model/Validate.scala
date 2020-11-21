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

  def findErrors(board: OpenSudokuBoard): Set[(Int, Int)] =
    (for {
      subset <- SudokuBoard.allSubsets(board.dim)
      errorValues = subset
        .flatMap(board.get)
        .groupBy(identity)
        .collect {
          case (key, values) if values.size > 1 => key
        }
        .toSet
      errorPosition <- subset.filter(pos => board.get(pos).exists(errorValues.contains))
    } yield errorPosition).toSet

  def correct(board: SolvedSudokuBoard): Boolean =
    SudokuBoard.allSubsets(board.dim).forall {
      _.map(board.get).sorted == SudokuBoard.values(board.dim)
    }
}
