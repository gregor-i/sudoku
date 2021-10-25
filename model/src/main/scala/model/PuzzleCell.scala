package model

sealed trait PuzzleCell {
  def solution: Int

  def isGiven: Boolean = this match {
    case PuzzleCell.Given(_) => true
    case _                   => false
  }

  def isNotGiven: Boolean = !isGiven

  def isInput: Boolean = this match {
    case PuzzleCell.CorrectInput(_)  => true
    case PuzzleCell.WrongInput(_, _) => true
    case _                           => false
  }

  def visible: Option[Int] = this match {
    case PuzzleCell.Given(value)         => Some(value)
    case PuzzleCell.CorrectInput(value)  => Some(value)
    case PuzzleCell.WrongInput(value, _) => Some(value)
    case PuzzleCell.Empty(_)             => None
  }

  def isCorrectAndFilled: Boolean = this match {
    case PuzzleCell.Given(_)        => true
    case PuzzleCell.CorrectInput(_) => true
    case _                          => false
  }

  def input(newValue: Option[Int]): PuzzleCell = {
    if (this.isGiven) throw new Exception("can't set value on an given cell")

    newValue match {
      case Some(input) if input == this.solution => PuzzleCell.CorrectInput(this.solution)
      case Some(input)                           => PuzzleCell.WrongInput(input, this.solution)
      case None                                  => PuzzleCell.Empty(this.solution)
    }
  }
}

object PuzzleCell {
  case class Given(solution: Int)                  extends PuzzleCell
  case class CorrectInput(solution: Int)           extends PuzzleCell
  case class WrongInput(input: Int, solution: Int) extends PuzzleCell
  case class Empty(solution: Int)                  extends PuzzleCell
}
