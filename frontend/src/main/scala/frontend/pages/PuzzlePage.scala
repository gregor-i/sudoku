package frontend.pages

import frontend.components._
import frontend.util.{Action, AsyncUtil}
import frontend.{NoRouting, Page, PageState}
import model._
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.components.{Button, ButtonList, Modal}
import snabbdom.toasts.{ToastType, Toasts}
import snabbdom.{Event, Node}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import scala.util.chaining.scalaUtilChainingOps

@Lenses
case class PuzzleState(
    seed: Int,
    desiredDifficulty: Difficulty,
    generatedBoard: OpenSudokuBoard,
    decoratedBoard: DecoratedBoard,
    focus: Option[Position],
    newPuzzleModalOpened: Boolean
) extends PageState

object PuzzleState {
  def process(seed: Int, desiredDifficulty: Difficulty, dimensions: Dimensions): Future[PuzzleState] =
    AsyncUtil.future {
      val generatedBoard = Generator(dimensions, seed, desiredDifficulty)
      val decoratedBoard = generatedBoard.map[DecoratedCell] {
        case None        => DecoratedCell.Empty
        case Some(value) => DecoratedCell.Given(value)
      }
      PuzzleState(
        seed = seed,
        desiredDifficulty = desiredDifficulty,
        generatedBoard = generatedBoard,
        decoratedBoard = decoratedBoard,
        focus = None,
        newPuzzleModalOpened = false
      )
    }

  def loading(seed: Int, desiredDifficulty: Difficulty, dimensions: Dimensions): LoadingState =
    LoadingState(process(seed, desiredDifficulty, dimensions))
}

object PuzzlePage extends Page[PuzzleState] with NoRouting {
  override def render(implicit context: Context): Node =
    Node("div.grid-layout")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(
              board = context.local.decoratedBoard,
              interaction = Some((pos, node) => {
                if (context.local.generatedBoard.get(pos).isEmpty)
                  node.event("click", Action(PuzzleState.focus.set(Some(pos))))
                else
                  node
              })
            ).classes("grid-main-svg")
          )
      )
      .child(buttonBar().classes("grid-footer"))
      .child(contextMenu())
      .maybeModify(context.local.newPuzzleModalOpened) {
        _.child(
          Modal(closeAction = Some(Action(PuzzleState.newPuzzleModalOpened.set(false))))(
            NewPuzzleModal(None)
          )
        )
      }
      .modify(
        InputContextMenu.globalEventListener(
          dim = context.local.generatedBoard.dim,
          focus = context.local.focus,
          setValue = (pos, value) => inputValue(pos, value),
          setFocus = pos => {
            if (context.local.generatedBoard.get(pos).isEmpty)
              context.update(PuzzleState.focus.set(Some(pos))(context.local))
            else ()
          }
        )
      )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList
      .right(
        Button(
          "New Game",
          Icons.generate,
          Action(PuzzleState.newPuzzleModalOpened.set(true))
        )
      )
      .classes("my-2")

  private def contextMenu()(implicit context: Context): Option[Node] =
    context.local.focus.map { pos =>
      InputContextMenu(
        dim = context.local.generatedBoard.dim,
        reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
        setFocus = pos => context.update(PuzzleState.focus.set(pos)(context.local)),
        setValue = value => inputValue(pos, value)
      )
    }

  private def inputValue(pos: Position, value: Option[Int])(implicit context: Context): Unit =
    context.update {
      val updatedBoard = context.local.decoratedBoard
        .set(pos, DecoratedCell.maybeInput(value))
        .pipe(DecoratedBoard.markMistakes)
      Validate(updatedBoard.map(_.toOption)) match {
        case Some(finishedGame) =>
          FinishedPuzzleState(
            board = finishedGame,
            difficulty = context.local.desiredDifficulty
          )
        case None =>
          context.local.copy(
            focus = None,
            decoratedBoard = updatedBoard
          )
      }
    }
}
