package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{NewPuzzleModal, SudokuBoardSVG}
import frontend.{GlobalState, Page, PageState}
import model.{DecoratedBoard, Dimensions, Generator, SudokuBoard}
import monocle.Lens
import snabbdom.Node
import snabbdom.components.Modal

case class LandingPageState(
    globalState: GlobalState
) extends PageState {
  def setGlobalState(globalState: GlobalState): LandingPageState = copy(globalState = globalState)
}

object LandingPage extends Page[LandingPageState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (globalState, "/", _)       => LandingPageState(globalState)
    case (globalState, "/puzzle", _) => LandingPageState(globalState)
    case (globalState, "/solver", _) => LandingPageState(globalState)
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] = Some(("/", Map.empty))

  override def render(using context: Context): Node =
    Modal(background = Some(background))(NewPuzzleModal(globalState.lastPuzzle))
      .classes("landing-page")

  private val backgroundBoard: DecoratedBoard = {
    val dim    = Dimensions(3, 3)
    val random = new scala.util.Random(0)
    import model.DecoratedCell._
    SudokuBoard.fill(dim) {
      pos =>
        val value = Generator.initialValue(dim)(pos)
        if (random.nextDouble() >= 0.66)
          Given(value)
        else
          Input(value)
    }
  }

  private val background: Node = SudokuBoardSVG(backgroundBoard).toNode
}
