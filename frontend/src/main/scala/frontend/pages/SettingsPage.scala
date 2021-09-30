package frontend.pages

import frontend.components.{Header, Icons}
import frontend.{Context, GlobalState, NoRouting, Page, PageState}
import model.{DecoratedBoard, Difficulty, Dimensions}
import monocle.{Lens, PLens}
import org.scalajs.dom.raw.HTMLSelectElement
import snabbdom.{Event, Node}
import snabbdom.components.{Button, Modal}

import scala.util.Random

case class SettingsState(modalOpened: Boolean = false)(implicit val globalState: GlobalState) extends PageState {
  def setGlobalState(globalState: GlobalState): SettingsState = copy()(globalState = globalState)
}

object SettingsState {
  val modalOpened = Lens[SettingsState, Boolean](_.modalOpened)(s => t => t.copy(modalOpened = s)(t.globalState))
}

object SettingsPage extends Page[SettingsState] with NoRouting {

  override def render(using context: Context): Node =
    "div.grid-layout"
      .key("SettingsPage")
      .child(Header.renderHeader())
      .child(
        "div.grid-main.my-2"
          .children(
            "h1.title".text(localized.settings),
            difficultyButtons(),
            dimensionButtons(),
            assistance()
          )
      )
      .child(
        "div.grid-footer.my-2.buttons"
          .child(
            playButtons(globalState.lastPuzzle)
          )
      )

  private def playButtons(lastPuzzle: Option[DecoratedBoard])(using Context) = {
    val playButton = Button(
      text = localized.playNewGame,
      icon = Icons.generate,
      onclick = setState(
        PuzzleState.loading(seed = Random.nextInt(), globalState.difficulty, globalState.dimensions)(using globalState)
      )
    ).style("flex", "auto 1")

    val continueButton = lastPuzzle.map(
      decoratedBoard =>
        Button(
          text = localized.continueLastGame,
          icon = Icons.continue,
          onclick = setState(PuzzleState.forBoard(decoratedBoard)(using globalState))
        ).classes("is-primary", "is-outlined", "is-light")
          .style("flex", "auto 1")
    )

    Node("div.buttons.my-2")
      .style("display", "flex")
      .style("width", "100%")
      .child(playButton)
      .childOptional(continueButton)
  }

  private def assistance()(using Context): Seq[Node] = {
    val options = Seq(
      localized.yes -> true,
      localized.no  -> false
    )

    Seq(
      "div.label".text(localized.highlightMistakes),
      Node("div.buttons")
        .style("display", "flex")
        .child(
          options.map {
            (text, value) =>
              Button(
                text = text,
                onclick = action(PageState.globalState.andThen(GlobalState.highlightMistakes).replace(value))
              ).style("flex", "auto 1")
                .maybeModify(globalState.highlightMistakes == value)(markButtonAsActive)
          }
        )
    )
  }

  private def difficultyButtons()(using context: Context) = {
    val difficulties = Seq(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

    Seq(
      "div.label".text(localized.difficultyLabel),
      Node("div.buttons")
        .style("display", "flex")
        .child(
          difficulties.map {
            diff =>
              Button(
                text = localized.difficulty(diff),
                icon = Icons.difficulty(diff),
                onclick = action(PageState.globalState.andThen(GlobalState.difficulty).replace(diff))
              ).style("flex", "auto 1")
                .maybeModify(globalState.difficulty == diff)(markButtonAsActive)
          }
        )
    )
  }

  private def dimensionButtons()(using context: Context) = {
    val dimensions = Seq(
      Dimensions(2, 2),
      Dimensions(2, 3),
      Dimensions(3, 3),
      Dimensions(3, 4)
    )

    Seq(
      Node("div.label").text(localized.sizeLabel),
      Node("div.buttons")
        .style("display", "flex")
        .child(
          dimensions.map {
            dim =>
              Button(
                text = s"1–${dim.blockSize}",
                onclick = action(PageState.globalState.andThen(GlobalState.dimensions).replace(dim))
              ).style("flex", "auto 1")
                .maybeModify(globalState.dimensions == dim)(markButtonAsActive)
          }
        )
    )
  }

  private val markButtonAsActive: Node => Node =
    _.classes("is-active").style("border-width", "2px")

}
