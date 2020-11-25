package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components._
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, Generator, OpenSudokuBoard, SudokuBoard}
import monocle.macros.Lenses
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Random

@Lenses
case class PuzzleState(
    seed: Int,
    generatedBoard: OpenSudokuBoard
) extends PageState

object PuzzleState {
  def load(seed: Int): PageState = LoadingState {
    AsyncUtil.future {
      val generatedBoard = Generator(Dimensions(3, 3), seed)
      PuzzleState(
        seed = seed,
        generatedBoard = generatedBoard
      )
    }
  }

}

object PuzzlePage extends Page[PuzzleState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp) if qp.get("page").forall(_ == "PuzzlePage") =>
      val seed = qp.get("seed").flatMap(_.toIntOption).getOrElse(1)
      PuzzleState.load(seed)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] =
    Some(("/", Map("page" -> "PuzzlePage", "seed" -> state.seed.toString) ++ QPHelper.OpenSudoku.toQP(state.generatedBoard)))

  override def render(implicit context: Context): Node =
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.is-flex-grow-1")
          .child(
            SudokuBoardSVG(context.local.generatedBoard.map(_.fold("")(_.toString)), Set.empty, None).classes("is-flex-grow-1")
          )
          .child(buttonBar())
      )

  private def buttonBar()(implicit context: Context): Node =
    ButtonList(
      Button(
        "New Game",
        Icons.generate,
        Snabbdom.event { _ =>
          def process =
            AsyncUtil.future {
              val seed           = Random.nextInt()
              val generatedBoard = Generator(context.local.generatedBoard.dim, seed)
              PuzzleState(
                seed = seed,
                generatedBoard = generatedBoard
              )
            }

          Toasts.futureToast("generating game ...", process) {
            case scala.util.Success(state) =>
              context.update(state)
              (frontend.toasts.Success, "Generated!")
            case scala.util.Failure(_) => (frontend.toasts.Danger, "Something went wrong ...")
          }
        }
      ).classes("is-primary", "mr-0")
    ).classes("my-2")
}
