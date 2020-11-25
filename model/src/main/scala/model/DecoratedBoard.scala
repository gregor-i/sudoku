package model

import scala.util.chaining._

object DecoratedBoard {
  def apply(openBoard: OpenSudokuBoard): DecoratedBoard =
    openBoard
      .map(DecoratedCell.maybeInput)
      .pipe(markMistakes)

  def markMistakes(decoratedBoard: DecoratedBoard): DecoratedBoard = {
    val mistakes = Validate.findMistakes(decoratedBoard.map(_.toOption))

    decoratedBoard.mapWithPosition {
      case (DecoratedCell.Input(value), pos) if mistakes.contains(pos)       => DecoratedCell.WrongInput(value)
      case (DecoratedCell.WrongInput(value), pos) if !mistakes.contains(pos) => DecoratedCell.Input(value)
      case (cell, _)                                                         => cell
    }
  }
}

sealed trait DecoratedCell {
  def toOption: Option[Int] = this match {
    case DecoratedCell.Given(value)      => Some(value)
    case DecoratedCell.Input(value)      => Some(value)
    case DecoratedCell.WrongInput(value) => Some(value)
    case DecoratedCell.Empty             => None
  }
}

object DecoratedCell {
  case class Given(value: Int)      extends DecoratedCell
  case class Input(value: Int)      extends DecoratedCell
  case class WrongInput(value: Int) extends DecoratedCell
  case object Empty                 extends DecoratedCell

  def maybeInput(value: Option[Int]): DecoratedCell = value.fold[DecoratedCell](DecoratedCell.Empty)(DecoratedCell.Input)
}
