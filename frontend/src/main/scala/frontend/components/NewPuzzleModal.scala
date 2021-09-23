package frontend.components

import frontend.{Context, GlobalState}
import frontend.pages.PuzzleState
import model.{DecoratedBoard, Difficulty, Dimensions}
import monocle.Lens
import snabbdom.Node
import snabbdom.components.Button
import frontend.PageState

import scala.util.Random

object NewPuzzleModal {
  def apply(lastPuzzle: Option[DecoratedBoard])(using context: Context[_]): Node =
    Node("div")
      .children(
        Node("div")
          .children(
            Node("h1.title.has-text-centered").text(localized.playSudoku),
            Node("div.label").text(localized.difficultyLabel),
            difficultyButtons(),
            Node("div.label").text(localized.sizeLabel),
            dimensionButtons()
          ),
        Node("div").style("height", "4rem"),
        Node("div").child(playButtons(lastPuzzle))
      )

  private def difficultyButtons()(using context: Context[_]) = {
    val difficulties = Seq(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

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
              .maybeModify(globalState.difficulty == diff) { _.classes("is-active").style("border-width", "2px") }
        }
      )
  }

  private def dimensionButtons()(using context: Context[_]) = {
    val dimensions = Seq(
      Dimensions(2, 2),
      Dimensions(2, 3),
      Dimensions(3, 3),
      Dimensions(3, 4)
    )

    Node("div.buttons")
      .style("display", "flex")
      .child(
        dimensions.map {
          dim =>
            Button(
              text = s"1–${dim.blockSize}",
              onclick = _ => action(PageState.globalState.andThen(GlobalState.dimensions).replace(dim))
            ).style("flex", "auto 1")
              .maybeModify(globalState.dimensions == dim) { _.classes("is-active").style("border-width", "2px") }
        }
      )
  }

  private def playButtons(lastPuzzle: Option[DecoratedBoard])(using context: Context[_]) = {

    val playButton = Button(
      text = localized.playNewGame,
      icon = Icons.generate,
      onclick = setState(
        PuzzleState.loading(seed = Random.nextInt(), globalState.difficulty, globalState.dimensions)
      )
    ).style("flex", "auto 1")

    val continueButton = lastPuzzle.map(
      decoratedBoard =>
        Button(
          text = localized.continueLastGame,
          icon = Icons.continue,
          onclick = setState(PuzzleState.forBoard(globalState, decoratedBoard))
        ).classes("is-primary", "is-outlined", "is-light")
          .style("flex", "auto 1")
    )

    Node("div.buttons")
      .style("display", "flex")
      .child(playButton)
      .childOptional(continueButton)
  }

}
