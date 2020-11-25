package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, Generator, OpenSudokuBoard, Position}
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

@Lenses
case class PuzzleState(
    seed: Int,
    generatedBoard: OpenSudokuBoard,
    focus: Option[Position]
) extends PageState

object PuzzleState {
  def process(seed: Int): Future[PuzzleState] =
    AsyncUtil.future {
      val generatedBoard = Generator(Dimensions(3, 3), seed)
      PuzzleState(
        seed = seed,
        generatedBoard = generatedBoard,
        focus = None
      )
    }

  def loading(seed: Int): LoadingState = LoadingState(process(seed))
}

object PuzzlePage extends Page[PuzzleState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp) if qp.get("page").forall(_ == "PuzzlePage") =>
      val seed = qp.get("seed").flatMap(_.toIntOption).getOrElse(1)
      PuzzleState.loading(seed)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(("/", Map("page" -> "PuzzlePage", "seed" -> state.seed.toString) ++ QPHelper.OpenSudoku.toQP(state.generatedBoard)))

  override def render(implicit context: Context): Node =
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.is-flex-grow-1")
          .child(
            SudokuBoardSVG(
              board = context.local.generatedBoard.map(_.fold("")(_.toString)),
              errorPositions = Set.empty,
              interaction = Some((pos, node) => node.event("click", Action(PuzzleState.focus.set(Some(pos)))))
            ).classes("is-flex-grow-1")
          )
          .child(buttonBar())
      )
      .child(contextMenu())

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button(
        "New Game",
        Icons.generate,
        Snabbdom.event { _ =>
          Toasts.futureToast("generating game ...", PuzzleState.process(Random.nextInt())) {
            case scala.util.Success(state) =>
              context.update(state)
              (frontend.toasts.Success, "Generated!")
            case scala.util.Failure(_) => (frontend.toasts.Danger, "Something went wrong ...")
          }
        }
      ).classes("is-primary", "mr-0")
    ).classes("my-2")

  private def contextMenu()(implicit context: Context): Option[Node] =
    context.local.focus.map { pos =>
      InputContextMenu(
        dim = context.local.generatedBoard.dim,
        reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
        setFocus = pos => context.update(PuzzleState.focus.set(pos)(context.local)),
        setValue = value =>
          context.update(
            PuzzleState.focus.set(None) andThen PuzzleState.generatedBoard.modify(_.set(pos, value)) apply context.local
          )
      )
    }
}
