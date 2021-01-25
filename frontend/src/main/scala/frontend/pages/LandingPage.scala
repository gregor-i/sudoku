package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Icons, SudokuBoardSVG}
import frontend.{GlobalState, Page, PageState}
import model.{DecoratedBoard, Difficulty, Dimensions, SudokuBoard}
import snabbdom.Node
import snabbdom.components.{Button, Modal}

import scala.util.Random

case class LandingPageState() extends PageState

object LandingPage extends Page[LandingPageState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = {
    case (_, "/", _)       => LandingPageState()
    case (_, "/puzzle", _) => LandingPageState()
    case (_, "/solver", _) => LandingPageState()
  }

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] = Some(("/", Map.empty))

  override def render(implicit context: Context): Node =
    Node("div.landing-page")
      .child(
        Modal(background = Some(background))(
          Node("div.landing-page-modal")
            .children(
              Node("div").children(
                Node("h1.title").text("Play Sudoku"),
                Node("div.big-buttons")
                  .childOptional(
                    context.global.lastPuzzle.map(
                      puzzleState =>
                        Button(
                          text = "Continue last Game",
                          icon = Icons.continue,
                          onclick = _ => context.update(puzzleState)
                        ).classes("is-primary", "is-outlined", "is-light")
                    )
                  )
                  .children(
                    Button(
                      text = "Easy",
                      icon = Icons.easy,
                      onclick = _ => context.update(PuzzleState.loading(seed = Random.nextInt(), Difficulty.Easy))
                    ),
                    Button(
                      text = "Medium",
                      icon = Icons.medium,
                      onclick = _ => context.update(PuzzleState.loading(seed = Random.nextInt(), Difficulty.Medium))
                    ),
                    Button(
                      text = "Hard",
                      icon = Icons.hard,
                      onclick = _ => context.update(PuzzleState.loading(seed = Random.nextInt(), Difficulty.Hard))
                    )
                  )
              ),
              Node("div").children(
                Node("h1.title").text("Solve Sudoku"),
                Node("div.big-buttons")
                  .child(
                    Button(
                      text = "Solver",
                      icon = Icons.solve,
                      onclick = _ => context.update(SolverState.empty())
                    )
                  )
              )
            )
        )
      )

  /*
    Generated with:
    import model._
    val given = Generator.apply(dim = Dimensions(3,3), seed = 1, desiredDifficulty = 2)
    val solution = FPSolver.apply(given).head
    val background = given.mapWithPosition{ case (Some(given), _) => DecoratedCell.Given(given); case (None, pos) => DecoratedCell.Input(solution.get(pos)) }
   */
  private val backgroundBoard: DecoratedBoard = {
    import model.DecoratedCell._
    SudokuBoard(
      dim = Dimensions(3, 3),
      data = Vector(
        Input(1),
        Given(8),
        Given(7),
        Input(6),
        Input(2),
        Input(4),
        Input(5),
        Input(9),
        Input(3),
        Input(6),
        Input(3),
        Given(4),
        Input(5),
        Input(9),
        Input(7),
        Given(1),
        Given(2),
        Input(8),
        Given(5),
        Input(2),
        Input(9),
        Given(3),
        Input(1),
        Input(8),
        Input(6),
        Input(7),
        Input(4),
        Input(8),
        Input(6),
        Input(1),
        Given(4),
        Given(7),
        Input(2),
        Given(3),
        Input(5),
        Input(9),
        Input(4),
        Input(7),
        Given(5),
        Given(9),
        Given(6),
        Input(3),
        Input(8),
        Input(1),
        Input(2),
        Input(2),
        Given(9),
        Input(3),
        Input(8),
        Input(5),
        Input(1),
        Given(4),
        Input(6),
        Input(7),
        Given(7),
        Input(4),
        Input(2),
        Given(1),
        Input(8),
        Input(6),
        Input(9),
        Input(3),
        Given(5),
        Input(3),
        Input(5),
        Input(6),
        Input(7),
        Input(4),
        Input(9),
        Given(2),
        Given(8),
        Input(1),
        Given(9),
        Given(1),
        Input(8),
        Input(2),
        Input(3),
        Input(5),
        Input(7),
        Input(4),
        Given(6)
      )
    )
  }

  private val background: Node =
    SudokuBoardSVG.apply(backgroundBoard, interaction = None)
}
