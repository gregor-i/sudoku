package frontend

import io.circe.generic.auto._
import frontend.language.Language
import io.circe.{Decoder, Encoder, JsonObject}
import model.{DecoratedBoard, Difficulty, Dimensions}
import monocle.Lens

case class GlobalState(
    lastPuzzle: Option[DecoratedBoard],
    difficulty: Difficulty,
    dimensions: Dimensions,
    language: Language,
    highlightMistakes: Boolean
)

object GlobalState {
  val lastPuzzle        = Lens[GlobalState, Option[DecoratedBoard]](_.lastPuzzle)(s => _.copy(lastPuzzle = s))
  val highlightMistakes = Lens[GlobalState, Boolean](_.highlightMistakes)(s => _.copy(highlightMistakes = s))
  val difficulty        = Lens[GlobalState, Difficulty](_.difficulty)(s => _.copy(difficulty = s))
  val dimensions        = Lens[GlobalState, Dimensions](_.dimensions)(s => _.copy(dimensions = s))

  val initial: GlobalState = GlobalState(
    lastPuzzle = None,
    difficulty = Difficulty.Medium,
    dimensions = Dimensions(3, 3),
    language = Language.detect.getOrElse(Language.default),
    highlightMistakes = true
  )

  implicit val encoder: Encoder[GlobalState] =
    io.circe.generic.semiauto.deriveEncoder[GlobalState]

  implicit val decoder: Decoder[GlobalState] =
    Decoder[JsonObject].map {
      json =>
        GlobalState(
          lastPuzzle = json.apply("lastPuzzle").flatMap(_.as[DecoratedBoard].toOption),
          difficulty = json.apply("difficulty").flatMap(_.as[Difficulty].toOption).getOrElse(initial.difficulty),
          dimensions = json.apply("dimensions").flatMap(_.as[Dimensions].toOption).getOrElse(initial.dimensions),
          language = json.apply("language").flatMap(_.as[Language].toOption).getOrElse(initial.language),
          highlightMistakes = json.apply("highlightMistakes").flatMap(_.as[Boolean].toOption).getOrElse(initial.highlightMistakes)
        )
    }
}
