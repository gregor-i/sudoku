package frontend.pages

import frontend.components.*
import frontend.util.AsyncUtil
import frontend.{Context, GlobalState, Page, PageState}
import model.*
import model.infinite.ContinuePuzzle
import model.solver.{Hint, NextInputHint, WrongInputHint}
import monocle.Lens
import org.scalajs.dom.document
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PuzzleState(
    board: SudokuPuzzle,
    focus: Option[Position],
    hint: Option[Hint]
)(implicit val globalState: GlobalState)
    extends PageState {
  def setGlobalState(globalState: GlobalState): PuzzleState = copy()(globalState = globalState)
}

object PuzzleState {
  val focus = Lens[PuzzleState, Option[Position]](_.focus)(s => t => t.copy(focus = s)(t.globalState))

  def forBoard(puzzle: SudokuPuzzle)(using GlobalState): PuzzleState =
    PuzzleState(
      board = puzzle,
      focus = None,
      hint = None
    )

  def process(seed: Long, desiredDifficulty: Difficulty, dimensions: Dimensions)(using GlobalState): Future[PuzzleState] =
    AsyncUtil.future {
      val board = Generator(dimensions, seed, desiredDifficulty)
      forBoard(board)
    }

  def loading(seed: Long, desiredDifficulty: Difficulty, dimensions: Dimensions)(using GlobalState): LoadingState =
    LoadingState(process(seed, desiredDifficulty, dimensions))
}

object PuzzlePage extends Page[PuzzleState] {
  override def render(using context: Context): Node =
    Node("div.grid-layout.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(pageState.board)
              .extendRects(contextMenuTriggerExtension(pageState.board))
              .extendRects(hintExtension(pageState.hint))
              .toNode
              .classes("grid-main-svg")
          )
      )
      .child(buttonBar().classes("grid-footer"))
      .child(contextMenu())

  private def contextMenuTriggerExtension(board: SudokuPuzzle)(using context: Context): SudokuBoardSVG.Extension =
    (pos, node) =>
      node
        .maybeModify(board.get(pos).isNotGiven)(_.event("click", action(PuzzleState.focus.replace(Some(pos)))))

  private def hintExtension(hint: Option[Hint]): SudokuBoardSVG.Extension =
    hint match {
      case Some(hint: NextInputHint) =>
        (pos, node) =>
          node
            .maybeModify(pos == hint.position)(_.classes("highlight-strong"))
            .maybeModify(hint.blockingPositions.contains(pos))(_.classes("highlight-weak"))
      case Some(hint: WrongInputHint) =>
        (pos, node) =>
          node
            .maybeModify(pos == hint.position)(_.classes("wrong-value"))
            .maybeModify(hint.relatedPositions.contains(pos))(_.classes("wrong-value-weak"))
      case None => SudokuBoardSVG.emptyExtension
    }

  private def buttonBar()(using context: Context): Node =
    ButtonList
      .right(Button(localized.hint, Icons.hint, action(giveHint)))
      .classes("my-2")

  private def giveHint(state: PuzzleState): PuzzleState = {
    val hint = Hint.of(state.board)
    state.copy(hint = hint)(state.globalState)
  }

  private def contextMenu()(using context: Context): Option[Node] =
    pageState.focus
      .map {
        pos =>
          InputContextMenu(
            focus = pos,
            dim = pageState.board.dim,
            reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
            setFocus = pos => context.update(PuzzleState.focus.replace(pos)(pageState)),
            setValue = value => inputValue(pos, value)
          )
      }

  private def inputValue(pos: Position, value: Option[Int])(using context: Context): Unit = {
    val updatedBoard = pageState.board.mod(pos, _.input(value))

    val newState = if (updatedBoard.data.forall(_.isCorrectAndFilled) && !globalState.infinitePuzzles) {
      FinishedPuzzleState(board = updatedBoard.map {
        case cell: PuzzleCell.Given        => cell
        case cell: PuzzleCell.CorrectInput => cell
        case _                             => throw new Error(s"this can't be")
      })(GlobalState.lastPuzzle.replace(None)(globalState))

    } else if (globalState.infinitePuzzles) {
      val continutedBoard =
        ContinuePuzzle.maybeContinue(updatedBoard, seed = scala.util.Random.nextInt(), globalState.difficulty)
      pageState.copy(
        focus = None,
        hint = None,
        board = continutedBoard.getOrElse(updatedBoard)
      )(GlobalState.lastPuzzle.replace(Some(updatedBoard))(globalState))

    } else {
      pageState.copy(
        focus = None,
        hint = None,
        board = updatedBoard
      )(GlobalState.lastPuzzle.replace(Some(updatedBoard))(globalState))
    }

    context.update(newState)
  }
}
