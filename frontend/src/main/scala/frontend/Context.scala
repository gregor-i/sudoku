package frontend

import frontend.language.Language
import frontend.pages.PuzzleState
import io.circe.generic.auto._
import io.circe.{Decoder, Encoder, JsonObject}
import model.{Difficulty, Dimensions}
import monocle.Lens

case class GlobalState(
    lastPuzzle: Option[PuzzleState],
    difficulty: Difficulty,
    dimensions: Dimensions,
    language: Language,
    highlightMistakes: Boolean
)

object GlobalState {
  val lastPuzzle        = Lens[GlobalState, Option[PuzzleState]](_.lastPuzzle)(s => _.copy(lastPuzzle = s))
  val highlightMistakes = Lens[GlobalState, Boolean](_.highlightMistakes)(s => _.copy(highlightMistakes = s))

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
          lastPuzzle = json.apply("lastPuzzle").flatMap(_.as[PuzzleState].toOption),
          difficulty = json.apply("difficulty").flatMap(_.as[Difficulty].toOption).getOrElse(initial.difficulty),
          dimensions = json.apply("dimensions").flatMap(_.as[Dimensions].toOption).getOrElse(initial.dimensions),
          language = json.apply("language").flatMap(_.as[Language].toOption).getOrElse(initial.language),
          highlightMistakes = json.apply("highlightMistakes").flatMap(_.as[Boolean].toOption).getOrElse(initial.highlightMistakes)
        )
    }
}

trait PageState

trait Context[+S <: PageState] {
  def local: S
  def update(pageState: PageState): Unit

  def global: GlobalState
  def update(globalState: GlobalState): Unit

  def update(globalState: GlobalState, pageState: PageState): Unit
}

object Context {
  def apply(pageState: PageState, globalState: GlobalState, renderState: (GlobalState, PageState) => Unit): Context[PageState] =
    new Context[PageState] {
      def local: PageState                  = pageState
      def update(newState: PageState): Unit = renderState(globalState, newState)

      def global: GlobalState                 = globalState
      def update(newState: GlobalState): Unit = renderState(newState, pageState)

      def update(newGlobalState: GlobalState, newPageState: PageState): Unit = renderState(newGlobalState, newPageState)
    }
}
