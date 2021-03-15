package model

import scala.util.chaining._

object DecoratedBoard {
  def apply(openBoard: OpenSudokuBoard): DecoratedBoard =
    openBoard
      .map(DecoratedCell.maybeInput)
      .pipe(markMistakes)

  def markMistakes(decoratedBoard: DecoratedBoard): DecoratedBoard = {
    val openBoard = decoratedBoard.map(_.toOption)

    SudokuBoard.fill(decoratedBoard.dim) { pos =>
      decoratedBoard.get(pos) match {
        case DecoratedCell.Input(value) if !Validate.noError(openBoard, pos)     => DecoratedCell.WrongInput(value)
        case DecoratedCell.WrongInput(value) if Validate.noError(openBoard, pos) => DecoratedCell.Input(value)
        case cell                                                                => cell
      }
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

  def isGiven: Boolean = this match {
    case DecoratedCell.Given(_)      => true
    case DecoratedCell.Input(_)      => false
    case DecoratedCell.WrongInput(_) => false
    case DecoratedCell.Empty         => false
  }

  def isNotGiven: Boolean = !isGiven
}

object DecoratedCell {
  case class Given(value: Int)      extends DecoratedCell
  case class Input(value: Int)      extends DecoratedCell
  case class WrongInput(value: Int) extends DecoratedCell
  case object Empty                 extends DecoratedCell

  def maybeInput(value: Option[Int]): DecoratedCell = value.fold[DecoratedCell](DecoratedCell.Empty)(DecoratedCell.Input)
}
