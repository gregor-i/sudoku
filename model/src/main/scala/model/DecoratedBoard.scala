package model

object DecoratedBoard {
  def apply(openBoard: OpenSudokuBoard): DecoratedBoard =
    openBoard.map(DecoratedCell.maybeInput)
}

sealed trait DecoratedCell {
  def toOption: Option[Int] = this match {
    case DecoratedCell.Given(value) => Some(value)
    case DecoratedCell.Input(value) => Some(value)
    case DecoratedCell.Empty        => None
  }

  def isGiven: Boolean = this match {
    case DecoratedCell.Given(_) => true
    case DecoratedCell.Input(_) => false
    case DecoratedCell.Empty    => false
  }

  def isNotGiven: Boolean = !isGiven
}

object DecoratedCell {
  case class Given(value: Int) extends DecoratedCell
  case class Input(value: Int) extends DecoratedCell
  case object Empty            extends DecoratedCell

  def maybeInput(value: Option[Int]): DecoratedCell = value.fold[DecoratedCell](DecoratedCell.Empty)(DecoratedCell.Input)
}
