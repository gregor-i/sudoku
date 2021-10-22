package frontend.language

import model.Difficulty

object German extends Language {
  override def playSudoku: String = "Sudoku spielen"

  override def difficultyLabel: String = "Schwierigkeit:"

  override def difficulty(difficulty: Difficulty): String = difficulty match {
    case Difficulty.Easy   => "Leicht"
    case Difficulty.Medium => "Mittel"
    case Difficulty.Hard   => "Schwer"
  }

  override def sizeLabel: String        = "Größe:"
  override def playNewGame: String      = "Neues Spiel starten"
  override def continueLastGame: String = "Letztes Spiel fortsetzen"

  override def hint: String = "Hinweis"

  override def settings: String = "Einstellungen:"

  override def highlightMistakes: String = "Falsche Eingaben hervorheben:"
  override def infinitePuzzles: String   = "Unendliche Sudokus:"

  override def yes: String = "Ja"

  override def no: String = "Nein"
}
