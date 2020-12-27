package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.Icons
import frontend.{GlobalState, Page, PageState}
import model.Difficulty
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
        Modal(_ => ())(
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
                      onclick = _ => context.update(PuzzleState.loading(seed = Random.nextInt(), Difficulty.easy))
                    ),
                    Button(
                      text = "Medium",
                      icon = Icons.medium,
                      onclick = _ => context.update(PuzzleState.loading(seed = Random.nextInt(), Difficulty.medium))
                    ),
                    Button(
                      text = "Hard",
                      icon = Icons.hard,
                      onclick = _ => context.update(PuzzleState.loading(seed = Random.nextInt(), Difficulty.medium))
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
}
