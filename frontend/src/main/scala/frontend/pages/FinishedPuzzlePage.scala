package frontend.pages

import frontend.components.{Header, Icons, NewPuzzleModal, SudokuBoardSVG}
import frontend.util.Action
import frontend.{NoRouting, Page, PageState}
import model._
import monocle.macros.Lenses
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

import scala.util.Random

@Lenses
case class FinishedPuzzleState(
    difficulty: Difficulty,
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
            SudokuBoardSVG(
              board = context.local.board.map(DecoratedCell.Input),
              interaction = Some(animations(context.local.board.dim))
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

  private def animations(dim: Dimensions)(pos: Position, node: Node): Node = {
    def d(p: Position): Double = {
      val d = (p._1 - pos._1, p._2 - pos._2)
      Math.sqrt(d._1 * d._1 + d._2 * d._2)
    }

    val random = new Random(dim.hashCode())
    val poss   = SudokuBoard.positions(dim)
    val bloom1 = random.shuffle(poss).head
    val bloom2 = random.shuffle(poss).head
    val bloom3 = random.shuffle(poss).head

    val speed = 0.25

    node.style(
      "animation",
      Seq(
        s"finished-animation1 6s linear ${d(bloom1) * speed + -6}s infinite both",
        s"finished-animation2 6s linear ${d(bloom2) * speed + -4}s infinite both",
        s"finished-animation3 6s linear ${d(bloom3) * speed + -2}s infinite both"
      ).mkString(",")
    )
  }
}
