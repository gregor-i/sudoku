package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model._
import monocle.macros.Lenses
import org.scalajs.dom.document
import snabbdom.{Node, Snabbdom, SnabbdomFacade}

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

object PuzzlePage extends Page[PuzzleState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/puzzle", qp) =>
      val seed              = qp.get("seed").flatMap(_.toIntOption).getOrElse(1)
      val desiredDifficulty = qp.get("desiredDifficulty").flatMap(_.toDoubleOption).getOrElse(Difficulty.default)
      PuzzleState.loading(seed, desiredDifficulty)
  }

  // todo: store input state
  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(("/puzzle", Map("seed" -> state.seed.toString, "desiredDifficulty" -> state.desiredDifficulty.toString)))

  override def render(implicit context: Context): Node =
    Node("div.grid-layout")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(
              board = DecoratedBoard.markMistakes(context.local.decoratedBoard),
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
      .childOptional(finishedModal())
      .pipe(
        InputContextMenu.globalEventListener(
          dim = context.local.generatedBoard.dim,
          focus = context.local.focus,
          setValue = (pos, value) => {
            val clearFocus = PuzzleState.focus.set(None)
            val set        = PuzzleState.decoratedBoard.modify(_.set(pos, DecoratedCell.maybeInput(value)))
            context.update((set andThen clearFocus).apply(context.local))
          },
          setFocus = pos => {
            if (context.local.generatedBoard.get(pos).isEmpty)
              context.update(PuzzleState.focus.set(Some(pos))(context.local))
            else ()
          }
        )
      )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button(
        "New Game",
        Icons.generate,
        generateGameAction(Random.nextInt())
      ).classes("mr-0")
    ).classes("my-2")

  private def generateGameAction(seed: Int)(implicit context: Context): SnabbdomFacade.Eventlistener =
    Snabbdom.event { _ =>
      Toasts.futureToast("generating game ...", PuzzleState.process(seed, context.local.desiredDifficulty)) {
        case scala.util.Success(state) =>
          context.update(state)
          (frontend.toasts.Success, "Generated!")
        case scala.util.Failure(_) => (frontend.toasts.Danger, "Something went wrong ...")
      }
    }

  private def contextMenu()(implicit context: Context): Option[Node] =
    context.local.focus.map { pos =>
      InputContextMenu(
        dim = context.local.generatedBoard.dim,
        reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
        setFocus = pos => context.update(PuzzleState.focus.set(pos)(context.local)),
        setValue = value =>
          context.update(
            PuzzleState.focus.set(None) andThen PuzzleState.decoratedBoard
              .modify(_.set(pos, DecoratedCell.maybeInput(value))) apply context.local
          )
      )
    }

  private def finishedModal()(implicit context: Context): Option[Node] =
    Validate(context.local.decoratedBoard.map(_.toOption)).map { _ =>
      Modal(Snabbdom.event(_ => ()))(
        Node("h1.title.has-text-centered").text("Sudoku completed!"),
        ButtonList.centered(
          Button("Back to landing Page", Snabbdom.event(_ => context.update(LandingPageState()))),
          Button("Next Game!", Icons.generate, generateGameAction(Random.nextInt()))
            .classes("is-primary")
        )
      )
    }
}
