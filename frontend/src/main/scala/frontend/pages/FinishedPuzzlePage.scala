package frontend.pages

import frontend.components.{Header, Icons, NewPuzzleModal, SudokuBoardSVG}
import frontend.util.Action
import frontend.{NoRouting, Page, PageState}
import model.DecoratedCell.{Given, Input}
import model._
import monocle.macros.Lenses
import org.scalajs.dom
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

import scala.util.Random

@Lenses
case class FinishedPuzzleState(
    board: DecoratedBoard,
    tapped: Boolean = false
) extends PageState {
  require(board.data.forall {
    case Given(_) | Input(_) => true
    case _                   => false
  })
}

object FinishedPuzzlePage extends Page[FinishedPuzzleState] with NoRouting {

  override def render(implicit context: Context): Node =
    Node("div.grid-layout")
      .key("FinishedPuzzlePage")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(
              board = context.local.board,
              interaction = Some(animations(context.local.board))
            ).classes("grid-main-svg", "finished-sudoku")
              .event("click", Action(FinishedPuzzleState.tapped.set(true)))
          )
      )
      .child(buttonBar().classes("grid-footer", "buttons", "my-2"))
      .maybeModify(context.local.tapped)(_.child(finishedModal()))

  private def finishedModal()(implicit context: Context): Node =
    Modal(closeAction = Some(Action(FinishedPuzzleState.tapped.set(false))))(
      NewPuzzleModal(None)
    )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList.right(
      Button("Next Game!", Icons.generate, Action(FinishedPuzzleState.tapped.set(true)))
        .classes("is-primary")
    )

  // note: there is an experimental API which would simplify this quite a lot:
  // https://developer.mozilla.org/en-US/docs/Web/API/Animation/Animation
  private def animations(sudokuBoard: SudokuBoard[_])(pos: Position, node: Node): Node = {
    def distance(p: Position, q: Position): Double = {
      val dx = p._1 - q._1
      val dy = p._2 - q._2
      Math.sqrt(dx * dx + dy * dy)
    }

    val dim = sudokuBoard.dim

    val random = new Random(sudokuBoard.hashCode())
    val bloomOrigins = Iterator.continually {
      val i = random.nextInt(dim.boardSize)
      SudokuBoard.positions(dim)(i)
    }

    val speed    = 0.15
    val duration = 0.8
    val pause    = speed * distance((0, 0), (dim.blockSize - 1, sudokuBoard.dim.blockSize - 1))

    var tick = 0

    def animation() =
      s"finished-animation ${duration}s linear ${distance(bloomOrigins.next(), pos) * speed + (duration + pause) * tick}s 1 both"

    var intervalHandle: Option[Int] = None

    node
      .hookInsert { elem =>
        elem.elm.get.style.animation = animation()
        intervalHandle = Some(
          dom.window.setInterval(
            () => {
              tick = tick + 1
              elem.elm.get.style.animation = animation()
            },
            (duration + pause) * 1000
          )
        )
      }
      .hookDestroy { _ =>
        intervalHandle.foreach(dom.window.clearInterval)
      }
  }
}
