package frontend.pages

import frontend.components.{Button, ButtonList, Header, Icons, Modal, SudokuBoardSVG}
import frontend.util.Action
import frontend.{NoRouting, Page, PageState}
import model.{DecoratedCell, Dimensions, Position, SolvedSudokuBoard, SudokuBoard}
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
            SudokuBoardSVG(
              board = context.local.board.map(DecoratedCell.Input),
              interaction = Some(animations(context.local.board.dim))
            ).classes("grid-main-svg", "finished-sudoku")
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

  private def animations(dim: Dimensions)(pos: Position, node: Node): Node = {
    def d(p: Position): Double = {
      val d = (p._1 - pos._1, p._2 - pos._2)
      Math.sqrt(d._1 * d._1 + d._2 * d._2)
    }

    def animationLine(bloom: Position, duration: Double, delay: Double) =
      s"finished-animation ${duration}s linear ${d(bloom) / 2.0 + delay}s infinite"

    val random    = new Random(dim.hashCode())
    val duration1 = random.nextDouble() * 2 + 2
    val duration2 = random.nextDouble() * 2 + 2
    val duration3 = random.nextDouble() * 2 + 2
    val poss      = SudokuBoard.positions(dim)
    val bloom1    = random.shuffle(poss).head
    val bloom2    = random.shuffle(poss).head
    val bloom3    = random.shuffle(poss).head

    node.style(
      "animation",
      Seq(
        animationLine(bloom1, duration1, 0.1),
        animationLine(bloom2, duration2, 0.2),
        animationLine(bloom3, duration3, 0.3)
      ).mkString(",")
    )
  }
}
