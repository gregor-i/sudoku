package frontend.components

import frontend.Context
import frontend.pages.PuzzleState
import model.{Difficulty, Dimensions}
import snabbdom.Node
import snabbdom.components.Button

import scala.util.Random

object NewPuzzleModal {
  def apply(lastPuzzle: Option[PuzzleState])(implicit context: Context[_]): Node =
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
        Node("div")
          .child(
            playButtons(lastPuzzle)
          )
      )

  private def difficultyButtons()(implicit context: Context[_]) = {
    val difficulties = Seq(Difficulty.Easy, Difficulty.Medium, Difficulty.Hard)

    Node("div.buttons")
      .style("display", "flex")
      .child(
        difficulties.map { diff =>
          Button(
            text = localized.difficulty(diff),
            icon = Icons.difficulty(diff),
            onclick = _ => context.update(context.global.copy(difficulty = diff))
          ).style("flex", "auto 1")
            .maybeModify(context.global.difficulty == diff) { _.classes("is-active").style("border-width", "2px") }
        }
      )
  }

  private def dimensionButtons()(implicit context: Context[_]) = {
    val dimensions = Seq(
      Dimensions(2, 2),
      Dimensions(2, 3),
      Dimensions(3, 3),
      Dimensions(3, 4)
    )

    Node("div.buttons")
      .style("display", "flex")
      .child(
        dimensions.map { dim =>
          Button(
            text = s"1–${dim.blockSize}",
            onclick = _ => context.update(context.global.copy(dimensions = dim))
          ).style("flex", "auto 1")
            .maybeModify(context.global.dimensions == dim) { _.classes("is-active").style("border-width", "2px") }
        }
      )
  }

  private def playButtons(lastPuzzle: Option[PuzzleState])(implicit context: Context[_]) = {

    val playButton = Button(
      text = localized.playNewGame,
      icon = Icons.generate,
      onclick = _ =>
        context.update(
          PuzzleState.loading(seed = Random.nextInt(), context.global.difficulty, context.global.dimensions)
        )
    ).style("flex", "auto 1")

    val continueButton = lastPuzzle.map(
      puzzleState =>
        Button(
          text = localized.continueLastGame,
          icon = Icons.continue,
          onclick = _ => context.update(puzzleState)
        ).classes("is-primary", "is-outlined", "is-light")
          .style("flex", "auto 1")
    )

    Node("div.buttons")
      .style("display", "flex")
      .child(playButton)
      .childOptional(continueButton)
  }

}
