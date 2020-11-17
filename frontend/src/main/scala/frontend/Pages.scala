package frontend

import frontend.pages._
import snabbdom.Node

object Pages {
  val all: Seq[Page[_ <: PageState]] = Seq(
    ErrorPage,
    SudokuSolverPage,
    SolvedSudokuPage
  )

  def selectPage[State <: PageState](nutriaState: State): Page[State] =
    all
      .find(_.acceptState(nutriaState))
      .map(_.asInstanceOf[Page[State]])
      .getOrElse(throw new Exception(s"No Page defined for '${nutriaState.getClass.getSimpleName}'"))

  def ui(context: Context[PageState]): Node =
    selectPage(context.local).render(context)
}
