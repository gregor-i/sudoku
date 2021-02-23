package frontend.language

import model.Difficulty

object English extends Language {
  def playSudoku: String = "Play Sudoku"

  override def difficultyLabel: String = "Difficulty:"

  override def difficulty(difficulty: Difficulty): String = difficulty match {
    case Difficulty.Easy   => "Easy"
    case Difficulty.Medium => "Medium"
    case Difficulty.Hard   => "Hard"
  }

  def sizeLabel: String = "Size: "

  def playNewGame: String = "Play new Game"

  def continueLastGame: String = "Continue last Game"

  override def settings: String = "Settings:"

  override def highlightMistakes: String = "Highlight wrong inputs:"

  override def yes: String = "Yes"

  override def no: String = "No"
}
