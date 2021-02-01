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
    tapped: Boolean = false,
    bloom: Position
) extends PageState {
  require(board.data.forall {
    case Given(_) | Input(_) => true
    case _                   => false
  })
}

object FinishedPuzzleState {
  def apply(board: DecoratedBoard): FinishedPuzzleState =
    FinishedPuzzleState(board, bloom = randomPosition(board.dim))

  def randomPosition(dim: Dimensions): Position = {
    val positions = SudokuBoard.positions(dim)
    positions((Math.random() * positions.length).toInt)
  }
}

object FinishedPuzzlePage extends Page[FinishedPuzzleState] with NoRouting {
  private object Animation {
    val speed = 0.15
    val duration = 0.8
    def pause(dim: Dimensions) = speed * (dim.blockSize - 1) * Math.sqrt(2d)
    def interval(dim: Dimensions): Double = duration + pause(dim)

    def bloomAnimation(bloom: Position)(pos: Position, node: Node): Node = {
      def distance(p: Position, q: Position): Double = {
        val dx = p._1 - q._1
        val dy = p._2 - q._2
        Math.sqrt(dx * dx + dy * dy)
      }

      node.style(
        "animation",
        s"finished-animation ${Animation.duration}s linear ${distance(bloom, pos) * Animation.speed}s 1 both"
      )
    }
  }

  override def render(implicit context: Context): Node =
    Node("div.grid-layout")
      .key("FinishedPuzzlePage")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(
              board = context.local.board,
              interaction = Some(Animation.bloomAnimation(context.local.bloom))
            ).classes("grid-main-svg", "finished-sudoku")
              .event("click", Action(FinishedPuzzleState.tapped.set(true)))
              .key(Math.random)
          )
      )
      .child(buttonBar().classes("grid-footer", "buttons", "my-2"))
      .maybeModify(context.local.tapped)(_.child(finishedModal()))
      .hookInsert { _ =>
        setTimeout()
      }
      .hookPostpatch { (_, _) =>
        setTimeout()
      }

  private def setTimeout()(implicit context: Context): Unit =
    dom.window.setTimeout(
      () =>
        context.update(context.local.copy(bloom = FinishedPuzzleState.randomPosition(context.local.board.dim)))
      ,
      Animation.interval(context.local.board.dim) * 1000
    )

  private def finishedModal()(implicit context: Context): Node =
    Modal(closeAction = Some(Action(FinishedPuzzleState.tapped.set(false))))(
      NewPuzzleModal(None)
    )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList.right(
      Button("Next Game!", Icons.generate, Action(FinishedPuzzleState.tapped.set(true)))
        .classes("is-primary")
    )

}
