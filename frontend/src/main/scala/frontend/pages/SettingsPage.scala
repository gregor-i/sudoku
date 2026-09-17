package frontend.pages

import frontend.components.{Button, Header, Icons}
import frontend.{Context, GlobalState, Page, PageState, Syntax}
import model.{Difficulty, Dimensions, SudokuPuzzle}
import monocle.{Lens, PLens}
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement

import scala.util.Random

case class SettingsState(oldGlobalState: GlobalState)(implicit val globalState: GlobalState) extends PageState {
  def setGlobalState(globalState: GlobalState): SettingsState = copy()(globalState = globalState)

  def changesAllowContinuing: Boolean =
    oldGlobalState.dimensions == globalState.dimensions &&
      oldGlobalState.difficulty == globalState.difficulty
}

object SettingsPage extends Page[SettingsState] {

  override def render(using context: Context): ReactiveHtmlElement.Base =
    div(
      cls := "grid-layout",
//      .key("SettingsPage")
      Header.renderHeader(),
      div(
        cls := "grid-main my-2",
        h1(cls := "title", textToTextNode(localized.settings)),
        difficultyButtons(),
        dimensionButtons(),
        infinitePuzzles()
      ),
      div(
        cls := "grid-footer my-2 buttons",
        buttons(globalState.lastPuzzle)
      )
    )

  private def buttons(lastPuzzle: Option[SudokuPuzzle])(using Context): ReactiveHtmlElement.Base = {
    val playButton = Button(
      localized.playNewGame,
      Icons.generate,
      setState(
        PuzzleState.loading(seed = Random.nextLong(), globalState.difficulty, globalState.dimensions)
      ),
      flex := "auto 1"
    )

    val continueButton = lastPuzzle match {
      case Some(decoratedBoard) if pageState.changesAllowContinuing =>
        Button(
          localized.continueLastGame,
          Icons.continue,
          setState(PuzzleState.forBoard(decoratedBoard)(using globalState)),
          cls  := "is-primary is-outlined is-light",
          flex := "auto 1"
        )
      case _ =>
        Button(
          localized.continueLastGame,
          Icons.continue,
          _ => (),
          disabled := true,
          flex     := "auto 1"
        )
    }

    div(
      cls := "buttons my-2",
      display.flex,
      width := "100%",
      playButton,
      continueButton
    )
  }

  private def infinitePuzzles()(using Context): Seq[ReactiveHtmlElement.Base] = {
    val options = Seq(
      localized.yes -> true,
      localized.no  -> false
    )

    Seq(
      div(cls := "label", textToTextNode(localized.infinitePuzzles)),
      div(
        cls := "buttons",
        display.flex,
        options.map {
          (text, value) =>
            Button(
              text,
              Syntax.action(PageState.globalState.andThen(GlobalState.infinitePuzzles).replace(value)),
              flex := "auto 1",
              if globalState.infinitePuzzles == value then markButtonAsActive else emptyMod
            )
        }
      )
    )
  }

  private def difficultyButtons()(using context: Context): Seq[ReactiveHtmlElement.Base] = {
    val difficulties = Seq(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

    Seq(
      div(cls := "label", textToTextNode(localized.difficultyLabel)),
      div(
        cls := "buttons",
        display.flex,
        difficulties.map {
          diff =>
            Button(
              localized.difficulty(diff),
              Icons.difficulty(diff),
              Syntax.action(PageState.globalState.andThen(GlobalState.difficulty).replace(diff)),
              flex := "auto 1",
              if globalState.difficulty == diff then markButtonAsActive else emptyMod
            )
        }
      )
    )
  }

  private def dimensionButtons()(using context: Context): Seq[ReactiveHtmlElement.Base] = {
    val dimensions = Seq(
      Dimensions(2, 2),
      Dimensions(2, 3),
      Dimensions(3, 3),
      Dimensions(3, 4)
    )

    Seq(
      div(cls := "abel", textToTextNode(localized.sizeLabel)),
      div(
        cls := "buttons",
        display.flex,
        dimensions.map {
          dim =>
            Button(
              s"1–${dim.blockSize}",
              Syntax.action(PageState.globalState.andThen(GlobalState.dimensions).replace(dim)),
              flex := "auto 1",
              if globalState.dimensions == dim then markButtonAsActive else emptyMod
            )
        }
      )
    )
  }

  private val markButtonAsActive: Modifier[ReactiveHtmlElement.Base] =
    seqToModifier(
      Seq[Modifier[ReactiveHtmlElement.Base]](
        cls         := "is-active",
        borderWidth := "2px"
      )
    )

}
