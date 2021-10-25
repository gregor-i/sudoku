package frontend

import io.circe.generic.auto.*
import frontend.language.Language
import io.circe.{Decoder, Encoder, JsonObject}
import model.{Difficulty, Dimensions, SudokuPuzzle}
import monocle.Lens

case class GlobalState(
    lastPuzzle: Option[SudokuPuzzle],
    difficulty: Difficulty,
    dimensions: Dimensions,
    language: Language,
    infinitePuzzles: Boolean
)

object GlobalState {
  val lastPuzzle      = Lens[GlobalState, Option[SudokuPuzzle]](_.lastPuzzle)(s => _.copy(lastPuzzle = s))
  val difficulty      = Lens[GlobalState, Difficulty](_.difficulty)(s => _.copy(difficulty = s))
  val dimensions      = Lens[GlobalState, Dimensions](_.dimensions)(s => _.copy(dimensions = s))
  val infinitePuzzles = Lens[GlobalState, Boolean](_.infinitePuzzles)(s => _.copy(infinitePuzzles = s))

  val initial: GlobalState = GlobalState(
    lastPuzzle = None,
    difficulty = Difficulty.Medium,
    dimensions = Dimensions(3, 3),
    language = Language.detect.getOrElse(Language.default),
    infinitePuzzles = false
  )

  given encoder: Encoder[GlobalState] =
    io.circe.generic.semiauto.deriveEncoder[GlobalState]

  given decoder: Decoder[GlobalState] =
    Decoder[JsonObject].map {
      json =>
        GlobalState(
          lastPuzzle = json.apply("lastPuzzle").flatMap(_.as[SudokuPuzzle].toOption),
          difficulty = json.apply("difficulty").flatMap(_.as[Difficulty].toOption).getOrElse(initial.difficulty),
          dimensions = json.apply("dimensions").flatMap(_.as[Dimensions].toOption).getOrElse(initial.dimensions),
          language = json.apply("language").flatMap(_.as[Language].toOption).getOrElse(initial.language),
          infinitePuzzles = json.apply("infinitePuzzles").flatMap(_.as[Boolean].toOption).getOrElse(initial.infinitePuzzles)
        )
    }
}
