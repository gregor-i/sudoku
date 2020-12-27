package frontend.pages

import frontend.components._
import frontend.util.{Action, AsyncUtil}
import frontend.{NoRouting, Page, PageState}
import model._
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.components.{Button, ButtonList}
import snabbdom.toasts.{ToastType, Toasts}
import snabbdom.{Event, Node}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import scala.util.chaining.scalaUtilChainingOps

@Lenses
case class PuzzleState(
    seed: Int,
    desiredDifficulty: Double,
    actualDifficulty: Double,
    generatedBoard: OpenSudokuBoard,
    decoratedBoard: DecoratedBoard,
    focus: Option[Position]
) extends PageState

object PuzzleState {
  def process(seed: Int, desiredDifficulty: Double = Difficulty.default): Future[PuzzleState] =
    AsyncUtil.future {
      val generatedBoard = Generator(Dimensions(3, 3), seed, desiredDifficulty)
      val decoratedBoard = generatedBoard.map[DecoratedCell] {
        case None        => DecoratedCell.Empty
        case Some(value) => DecoratedCell.Given(value)
      }
      val actualDifficulty = Difficulty(generatedBoard).getOrElse(0.0)
      PuzzleState(
        seed = seed,
        desiredDifficulty = desiredDifficulty,
        actualDifficulty = actualDifficulty,
        generatedBoard = generatedBoard,
        decoratedBoard = decoratedBoard,
        focus = None
      )
    }

  def loading(seed: Int, desiredDifficulty: Double = Difficulty.default): LoadingState =
    LoadingState(process(seed, desiredDifficulty))
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
      .pipe(
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
          generateGameAction(Random.nextInt())
        )
      )
      .classes("my-2")

  private def generateGameAction(seed: Int)(implicit context: Context): Event => Unit =
    _ =>
      Toasts.asyncToast("generating game ...", PuzzleState.process(seed, context.local.desiredDifficulty)) {
        case scala.util.Success(state) =>
          context.update(state)
          (ToastType.Success, "Generated!")
        case scala.util.Failure(_) =>
          (ToastType.Danger, "Something went wrong ...")
      }

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
