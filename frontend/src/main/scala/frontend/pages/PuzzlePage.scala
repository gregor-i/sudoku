package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Button, ButtonList, Header, Icons, SudokuBoardSVG}
import frontend.toasts.Toasts
import frontend.util.AsyncUtil
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, Generator, OpenSudokuBoard, SolvedSudokuBoard, Solver, SudokuBoard, Validate}
import monocle.macros.Lenses
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Random

@Lenses
case class PuzzleState(
    seed: Int,
    generatedBoard: OpenSudokuBoard
) extends PageState

object PuzzlePage extends Page[PuzzleState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", qp) if qp.get("page").contains("PuzzlePage") =>
      val seed  = qp.get("seed").flatMap(_.toIntOption).getOrElse(1)
      val board = SudokuBoard.empty(Dimensions(3, 3)) //Generator(Dimensions(3, 3), seed)
      PuzzleState(
        seed = seed,
        generatedBoard = board
      )
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
