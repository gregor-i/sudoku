package frontend.pages

import frontend.components.{Button, ButtonList, Header, Icons, InputContextMenu, SudokuBoardSVG}
import frontend.util.AsyncUtil
import frontend.{Context, GlobalState, Page, PageState, Syntax}
import model.*
import model.infinite.ContinuePuzzle
import model.solver.{Hint, NextInputHint, WrongInputHint}
import monocle.Lens
import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom.document

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import frontend.Syntax.*

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

  def setAsync(seed: Long, desiredDifficulty: Difficulty, dimensions: Dimensions, storage: Var[PageState])(using
      GlobalState
  ): Future[Unit] =
    AsyncUtil.future {
      val board = Generator(dimensions, seed, desiredDifficulty)
      val state = forBoard(board)
      storage.set(state)
    }
}

object PuzzlePage extends Page[PuzzleState] {
  override def render(using context: Context): com.raquo.laminar.nodes.ReactiveHtmlElement[org.scalajs.dom.HTMLDivElement] =
    div(
      cls := "grid-layout no-scroll",
      Header.renderHeader(),
      div(
        cls := "grid-main",
        SudokuBoardSVG(context.local.board)
          .extendRects(contextMenuTriggerExtension(context.local.board))
          .extendRects(hintExtension(pageState.hint))
          .addClasses("grid-main-svg")
          .toNode
      ),
      contextMenu() match {
        case Some(menu) => menu
        case None       => emptyMod
      },
      buttonBar(classes = "grid-footer")
    )

  private def contextMenuTriggerExtension(board: SudokuPuzzle)(using context: Context): SudokuBoardSVG.Extension =
    pos =>
      if board.get(pos).isNotGiven then
        Seq(
          onClick.-->(_ => context.update(PuzzleState.focus.replace(Some(pos))(context.local)))
        )
      else Nil

  private def hintExtension(hint: Option[Hint]): SudokuBoardSVG.Extension =
    hint match {
      case Some(hint: NextInputHint) =>
        pos =>
          if pos == hint.position then
            Seq(svg.cls := "highlight-strong")
          else if hint.blockingPositions.contains(pos) then
            Seq(svg.cls := "highlight-weak")
          else Seq.empty
      case Some(hint: WrongInputHint) =>
        pos =>
          if pos == hint.position then
            Seq(svg.cls := "wrong-value")
          else if hint.relatedPositions.contains(pos) then
            Seq(svg.cls := "wrong-value-weak")
          else
          Seq.empty
      case None => SudokuBoardSVG.emptyExtension
    }

  private def buttonBar(classes: String)(using context: Context): Node =
    ButtonList
      .right(
        cls := classes,
        cls := "my-2",
        Button(localized.hint, Icons.hint, Syntax.action(giveHint))
      )

  private def giveHint(state: PuzzleState): PuzzleState = {
    val hint = Hint.of(state.board)
    state.copy(hint = hint)(state.globalState)
  }

  private def contextMenu()(using context: Context): Option[ReactiveHtmlElement[?]] =
    context.local.focus
      .map {
        pos =>
          InputContextMenu(
            focus = pos,
            dim = context.local.board.dim,
            reference = document.getElementById(s"cell_${pos._1}_${pos._2}"),
            setFocus = pos => context.update(PuzzleState.focus.replace(pos)(context.local)),
            setValue = value => inputValue(pos, value)
          )
      }

  private def inputValue(pos: Position, value: Option[Int])(using context: Context): Unit = {
    val updatedBoard = context.local.board.mod(pos, _.input(value))

    val newState = if (updatedBoard.data.forall(_.isCorrectAndFilled) && !context.global.infinitePuzzles) {
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
