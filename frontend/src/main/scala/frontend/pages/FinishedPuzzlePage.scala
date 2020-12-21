package frontend.pages

import frontend.components.{Button, ButtonList, Header, Icons, Modal, SudokuBoardSVG}
import frontend.util.Action
import frontend.{NoRouting, Page, PageState}
import model.{DecoratedCell, SolvedSudokuBoard}
import monocle.macros.Lenses
import snabbdom.{Event, Node}
import toasts.{ToastType, Toasts}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

@Lenses
case class FinishedPuzzleState(
    difficulty: Double,
    board: SolvedSudokuBoard,
    tapped: Boolean = false
) extends PageState

object FinishedPuzzlePage extends Page[FinishedPuzzleState] with NoRouting {

  override def render(implicit context: Context): Node =
    Node("div.grid-layout")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(board = context.local.board.map(DecoratedCell.Input), interaction = None)
              .classes("grid-main-svg")
              .event("click", Action(FinishedPuzzleState.tapped.set(true)))
          )
      )
      .child(buttonBar().classes("grid-footer", "buttons", "my-2"))
      .childOptional {
        if (context.local.tapped) Some(finishedModal())
        else None
      }

  private def finishedModal()(implicit context: Context): Node =
    Modal(Action(FinishedPuzzleState.tapped.set(false)))(
      Node("h1.title.has-text-centered").text("Sudoku completed!"),
      buttonBar()
    )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button("Change Difficulty", _ => context.update(LandingPageState())),
      Button("Next Game!", Icons.generate, generateGameAction(Random.nextInt()))
        .classes("is-primary")
    )

  private def generateGameAction(seed: Int)(implicit context: Context): Event => Unit =
    _ =>
      Toasts.asyncToast("generating game ...", PuzzleState.process(seed, context.local.difficulty)) {
        case scala.util.Success(state) =>
          context.update(state)
          (ToastType.Success, "Generated!")
        case scala.util.Failure(_) =>
          (ToastType.Danger, "Something went wrong ...")
      }
}
