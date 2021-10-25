package model

sealed trait PuzzleCell {
  def solution: Int

  def isGiven: Boolean = this match {
    case PuzzleCell.Given(_)    => true
    case PuzzleCell.Input(_, _) => false
    case PuzzleCell.Empty(_)    => false
  }

  def isNotGiven: Boolean = !isGiven

  def isInput: Boolean = this match {
    case PuzzleCell.Given(_)    => false
    case PuzzleCell.Input(_, _) => true
    case PuzzleCell.Empty(_)    => false
  }

  def visible: Option[Int] = this match {
    case PuzzleCell.Given(value)    => Some(value)
    case PuzzleCell.Input(value, _) => Some(value)
    case PuzzleCell.Empty(_)        => None
  }

  def isCorrectAndFilled: Boolean = this match {
    case PuzzleCell.Given(value)           => true
    case PuzzleCell.Input(input, solution) => input == solution
    case PuzzleCell.Empty(_)               => false
  }
}

object PuzzleCell {
  case class Given(solution: Int)             extends PuzzleCell
  case class Input(input: Int, solution: Int) extends PuzzleCell
  case class Empty(solution: Int)             extends PuzzleCell
}
