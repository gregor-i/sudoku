package frontend.pages

import frontend.components.*
import frontend.util.AsyncUtil
import frontend.{Context, GlobalState, NoRouting, Page, PageState}
import model.*
import model.solver.Hint
import monocle.Lens
import org.scalajs.dom.document
import snabbdom.Node
import snabbdom.components.{Button, ButtonList, Modal}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class PuzzleState(
    board: DecoratedBoard,
    focus: Option[Position],
    newPuzzleModalOpened: Boolean,
    hint: Option[Hint]
)(implicit val globalState: GlobalState)
    extends PageState {
  def setGlobalState(globalState: GlobalState): PuzzleState = copy()(globalState = globalState)
}

object PuzzleState {
  val newPuzzleModalOpened =
    Lens[PuzzleState, Boolean](_.newPuzzleModalOpened)(s => t => t.copy(newPuzzleModalOpened = s)(t.globalState))
  val focus = Lens[PuzzleState, Option[Position]](_.focus)(s => t => t.copy(focus = s)(t.globalState))

  def forBoard(decoratedBoard: DecoratedBoard)(using GlobalState): PuzzleState =
    PuzzleState(
      board = decoratedBoard,
      focus = None,
      newPuzzleModalOpened = false,
      hint = None
    )

  def process(seed: Int, desiredDifficulty: Difficulty, dimensions: Dimensions)(using GlobalState): Future[PuzzleState] =
    AsyncUtil.future {
      val board = Generator(dimensions, seed, desiredDifficulty)
        .map[DecoratedCell] {
          case None        => DecoratedCell.Empty
          case Some(value) => DecoratedCell.Given(value)
        }
      forBoard(board)
    }

  def loading(seed: Int, desiredDifficulty: Difficulty, dimensions: Dimensions)(using GlobalState): LoadingState =
    LoadingState(process(seed, desiredDifficulty, dimensions))
}

object PuzzlePage extends Page[PuzzleState] with NoRouting {
  override def render(using context: Context): Node =
    Node("div.grid-layout.no-scroll")
      .child(Header.renderHeader())
      .child(
        Node("div.grid-main")
          .child(
            SudokuBoardSVG(pageState.board)
              .extendRects(contextMenuTriggerExtension(pageState.board))
              .extendRects(
                SudokuBoardSVG.wrongNumbers(enabled = globalState.highlightMistakes, pageState.board)
              )
              .extendRects(hintExtension(pageState.hint))
              .toNode
              .classes("grid-main-svg")
          )
      )
      .child(buttonBar().classes("grid-footer"))
      .child(contextMenu())
      .maybeModify(pageState.newPuzzleModalOpened) {
        _.child(
          Modal(closeAction = Some(action(PuzzleState.newPuzzleModalOpened.replace(false))))(
            NewPuzzleModal(None)
          )
        )
      }

  private def contextMenuTriggerExtension(board: DecoratedBoard)(using context: Context): SudokuBoardSVG.Extension =
    (pos, node) =>
      node
        .maybeModify(board.get(pos).isNotGiven)(_.event("click", action(PuzzleState.focus.replace(Some(pos)))))

  private def hintExtension(hint: Option[Hint]): SudokuBoardSVG.Extension =
    hint match {
      case Some(hint) =>
        (pos, node) =>
          node
            .maybeModify(pos == hint.position)(_.classes("highlight-strong"))
            .maybeModify(hint.blockingPositions.contains(pos))(_.classes("highlight-weak"))
      case None => SudokuBoardSVG.emptyExtension
    }

  private def buttonBar()(using context: Context): Node =
    ButtonList
      .right(
        Button(localized.hint, Icons.hint, action(giveHint)),
        Button(
          localized.playNewGame,
          Icons.generate,
          action(PuzzleState.newPuzzleModalOpened.replace(true))
        )
      )
      .classes("my-2")

  private def giveHint(state: PuzzleState): PuzzleState = {
    val hint = Hint.of(state.board.map(_.toOption))
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
            setFocus = pos => action(PuzzleState.focus.replace(pos)),
            setValue = value => inputValue(pos, value)
          )
      }

  private def inputValue(pos: Position, value: Option[Int])(using context: Context): Unit = {
    val updatedBoard = pageState.board
      .set(pos, DecoratedCell.maybeInput(value))

    val continutedBoard = ContinuePuzzle.maybeContinue(updatedBoard, seed = scala.util.Random.nextInt(), globalState.difficulty)

    val newState = Validate(updatedBoard.map(_.toOption)) match {
      case Some(_) =>
        FinishedPuzzleState(board = continutedBoard)(GlobalState.lastPuzzle.replace(None)(globalState))
      case None =>
        pageState.copy(
          focus = None,
          hint = None,
          board = continutedBoard
        )(GlobalState.lastPuzzle.replace(Some(continutedBoard))(globalState))
    }

    context.update(newState)
  }
}
