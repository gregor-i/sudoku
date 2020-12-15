package frontend

import frontend.pages.PuzzleState
import monocle.macros.Lenses

@Lenses
case class GlobalState(
    lastPuzzle: Option[PuzzleState] = None
)

object GlobalState {
  def initial(): GlobalState = GlobalState()
}

trait PageState

trait Context[+S <: PageState] {
  def local: S
  def update(pageState: PageState): Unit

  def global: GlobalState
  def update(globalState: GlobalState): Unit

  def update(globalState: GlobalState, pageState: PageState): Unit
}

object Context {
  def apply(pageState: PageState, globalState: GlobalState, renderState: (GlobalState, PageState) => Unit): Context[PageState] =
    new Context[PageState] {
      def local: PageState                  = pageState
      def update(newState: PageState): Unit = renderState(globalState, newState)

      def global: GlobalState                 = globalState
      def update(newState: GlobalState): Unit = renderState(newState, pageState)

      def update(newGlobalState: GlobalState, newPageState: PageState): Unit = renderState(newGlobalState, newPageState)
    }
}
