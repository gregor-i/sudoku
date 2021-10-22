package frontend.language

import model.Difficulty

object English extends Language {
  override def playSudoku: String = "Play Sudoku"

  override def difficultyLabel: String = "Difficulty:"

  override def difficulty(difficulty: Difficulty): String = difficulty match {
    case Difficulty.Easy   => "Easy"
    case Difficulty.Medium => "Medium"
    case Difficulty.Hard   => "Hard"
  }

  override def sizeLabel: String        = "Size: "
  override def playNewGame: String      = "Start new Game"
  override def continueLastGame: String = "Continue last Game"
  override def hint: String             = "Hint"

  override def settings: String = "Settings:"

  override def highlightMistakes: String = "Highlight wrong inputs:"
  override def infinitePuzzles: String   = "Infinite Sudokus:"

  override def yes: String = "Yes"

  override def no: String = "No"
}
