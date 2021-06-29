package frontend.pages

import frontend.components._
import frontend.util.{Action, AsyncUtil}
import frontend.{GlobalState, NoRouting, Page, PageState}
import model._
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.chaining.scalaUtilChainingOps

@Lenses
case class PuzzleState(
    board: DecoratedBoard,
    focus: Option[Position],
    newPuzzleModalOpened: Boolean,
    numberHighlight: Option[Int]
) extends PageState

object PuzzleState {
  def process(seed: Int, desiredDifficulty: Difficulty, dimensions: Dimensions): Future[PuzzleState] =
    AsyncUtil.future {
      val board = Generator(dimensions, seed, desiredDifficulty)
        .map[DecoratedCell] {
          case None        => DecoratedCell.Empty
          case Some(value) => DecoratedCell.Given(value)
        }
      PuzzleState(
        board = board,
        focus = None,
        newPuzzleModalOpened = false,
        numberHighlight = None
      )
    }

  def loading(seed: Int, desiredDifficulty: Difficulty, dimensions: Dimensions): LoadingState =
    LoadingState(process(seed, desiredDifficulty, dimensions))
}

object PuzzlePage extends Page[PuzzleState] with NoRouting {
  override def render(implicit context: Context): Node =
    Node("div.grid-layout.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(
              board = context.local.board,
              extension = Some((pos, node) => {
                if (context.local.board.get(pos).isNotGiven)
                  node.event("click", Action(PuzzleState.focus.set(Some(pos))))
                else
                  node
              }),
              highlightMistakes = context.global.highlightMistakes,
              numberHighlight = context.local.numberHighlight
            ).classes("grid-main-svg")
          )
          .child(valueCounter(context.local.board))
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
          dim = context.local.board.dim,
          focus = context.local.focus,
          setValue = (pos, value) => inputValue(pos, value),
          setFocus = pos => {
            if (context.local.board.get(pos).isNotGiven)
              context.update(PuzzleState.focus.set(Some(pos))(context.local))
            else ()
          }
        )
      )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList
      .right(
        Button(
          localized.playNewGame,
          Icons.generate,
          Action(PuzzleState.newPuzzleModalOpened.set(true))
        )
      )
      .classes("my-2")

  private def contextMenu()(implicit context: Context): Option[Node] =
    context.local.focus.map { pos =>
      InputContextMenu(
        dim = context.local.board.dim,
        reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
        setFocus = pos => context.update(PuzzleState.focus.set(pos)(context.local)),
        setValue = value => inputValue(pos, value)
      )
    }

  private def inputValue(pos: Position, value: Option[Int])(implicit context: Context): Unit = {
    val updatedBoard = context.local.board
      .set(pos, DecoratedCell.maybeInput(value))

    Validate(updatedBoard.map(_.toOption)) match {
      case Some(_) =>
        context.update(
          globalState = GlobalState.lastPuzzle.set(None)(context.global),
          pageState = FinishedPuzzleState(
            board = updatedBoard
          )
        )
      case None =>
        val updatedPuzzleState = context.local.copy(
          focus = None,
          board = updatedBoard
        )

        context.update(
          globalState = GlobalState.lastPuzzle.set(Some(updatedPuzzleState))(context.global),
          pageState = updatedPuzzleState
        )
    }
  }

  private def valueCounter(board: DecoratedBoard)(implicit context: Context): Seq[Node] = {
    SudokuBoard
      .values(board.dim)
      .map { value =>
        val count = board.data
          .count(_.toOption.contains(value))

        val button = Node("a.button.is-rounded")
          .`class`("is-primary", context.local.numberHighlight.contains(value))
          .text(value.toString)
          .event("click", Action(PuzzleState.numberHighlight.modify {
            case Some(`value`) => None
            case _             => Some(value)
          }))

        count match {
          case 0 => button
          case n if n == board.dim.blockSize =>
            button.child(Node("span.tag.is-success.is-rounded").child("i.fas.fa-check"))
          case _ => button.child(Node("span.tag.is-info.is-rounded").text(count.toString))
        }
      }
  }
}
