package frontend.pages

import frontend.components.{Header, Icons, SudokuBoardSVG}
import frontend.{Page, PageState}
import model._
import monocle.Lens
import org.scalajs.dom
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

import scala.util.Random
import monocle.syntax.all._
import frontend.GlobalState

case class FinishedPuzzleState(board: FinishedSudokuPuzzle)(implicit val globalState: GlobalState) extends PageState {
  def setGlobalState(globalState: GlobalState): FinishedPuzzleState = copy()(globalState = globalState)
}

object FinishedPuzzlePage extends Page[FinishedPuzzleState] {

  override def render(using context: Context): Node =
    Node("div.grid-layout.no-scroll")
      .key("FinishedPuzzlePage")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(pageState.board)
              .extendRects(animations(pageState.board))
              .toNode
              .classes("grid-main-svg", "finished-sudoku")
          )
      )
      .child(buttonBar().classes("grid-footer", "buttons", "my-2"))

  private def buttonBar()(using context: Context): Node =
    ButtonList.right(
      Button(
        localized.playNewGame,
        Icons.generate,
        setState(
          PuzzleState.loading(
            seed = Random.nextLong(),
            dimensions = globalState.dimensions,
            desiredDifficulty = globalState.difficulty
          )(using globalState)
        )
      )
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

    val random       = new Random(sudokuBoard.hashCode())
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
      .hookInsert {
        elem =>
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
      .hookDestroy {
        _ =>
          intervalHandle.foreach(dom.window.clearInterval)
      }
  }
}
