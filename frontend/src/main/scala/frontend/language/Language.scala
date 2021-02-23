package frontend.language

import io.circe.{Decoder, Encoder}
import model.Difficulty
import org.scalajs.dom

trait Language {
  def sizeLabel: String
  def playSudoku: String
  def difficultyLabel: String
  def difficulty(difficulty: Difficulty): String
  def playNewGame: String
  def continueLastGame: String
  def settings: String
  def highlightMistakes: String
  def yes: String
  def no: String
}

object Language {
  def detect: Option[Language] =
    dom.window.navigator.language match {
      case lang if lang.startsWith("de") => Some(German)
      case lang if lang.startsWith("en") => Some(English)
      case _                             => None
    }

  val default: Language = English

  implicit val encoder: Encoder[Language] =
    Encoder.encodeString.contramap {
      case English => "en"
      case German  => "de"
    }

  implicit val decoder: Decoder[Language] =
    Decoder.decodeString.emap {
      case "en" => Right(English)
      case "de" => Right(German)
      case _    => Left("language unknown")
    }
}
