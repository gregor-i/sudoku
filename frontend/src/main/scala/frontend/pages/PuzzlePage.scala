package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model.{DecoratedBoard, DecoratedCell, Dimensions, Generator, OpenSudokuBoard, Position, SudokuBoard}
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random
import scala.util.chaining.scalaUtilChainingOps

@Lenses
case class PuzzleState(
    seed: Int,
    generatedBoard: SudokuBoard[DecoratedCell],
    focus: Option[Position]
) extends PageState

object PuzzleState {
  def process(seed: Int): Future[PuzzleState] =
    AsyncUtil.future {
      val generatedBoard = Generator(Dimensions(3, 3), seed).map[DecoratedCell] {
        case None        => DecoratedCell.Empty
        case Some(value) => DecoratedCell.Given(value)
      }
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
    case (_, "/", _) =>
      PuzzleState.loading(Random.nextInt())
    case (_, "/puzzle", qp) =>
      val seed = qp.get("seed").flatMap(_.toIntOption).getOrElse(1)
      PuzzleState.loading(seed)
  }

  // todo: store input state
  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(("/puzzle", Map("seed" -> state.seed.toString)))

  override def render(implicit context: Context): Node =
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.is-flex-grow-1")
          .child(
            SudokuBoardSVG(
              board = DecoratedBoard.markMistakes(context.local.generatedBoard),
              interaction = Some((pos, node) => node.event("click", Action(PuzzleState.focus.set(Some(pos)))))
            ).classes("is-flex-grow-1")
          )
          .child(buttonBar())
      )
      .child(contextMenu())
      .pipe(
        InputContextMenu.globalEventListener(
          dim = context.local.generatedBoard.dim,
          focus = context.local.focus,
          setValue = (pos, value) => {
            val clearFocus = PuzzleState.focus.set(None)
            val set        = PuzzleState.generatedBoard.modify(_.set(pos, DecoratedCell.maybeInput(value)))
            context.update((set andThen clearFocus).apply(context.local))
          },
          setFocus = pos => context.update(PuzzleState.focus.set(Some(pos))(context.local))
        )
      )

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
            PuzzleState.focus.set(None) andThen PuzzleState.generatedBoard
              .modify(_.set(pos, DecoratedCell.maybeInput(value))) apply context.local
          )
      )
    }
}
