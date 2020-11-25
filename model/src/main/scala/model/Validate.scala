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

  def findMistakes(board: OpenSudokuBoard): Set[(Int, Int)] =
    (for {
      subset <- SudokuBoard.allSubsets(board.dim)
      mistakeValues = subset
        .flatMap(board.get)
        .groupBy(identity)
        .collect {
          case (key, values) if values.size > 1 => key
        }
        .toSet
      mistakePosition <- subset.filter(pos => board.get(pos).exists(mistakeValues.contains))
    } yield mistakePosition).toSet

  def correct(board: SolvedSudokuBoard): Boolean =
    SudokuBoard.allSubsets(board.dim).forall {
      _.map(board.get).sorted == SudokuBoard.values(board.dim)
    }
}
