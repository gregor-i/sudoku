package frontend

import model.{Difficulty, Dimensions}
import monocle.Lens

sealed trait Context[+S <: PageState] {
  def local: S
  def update(pageState: PageState): Unit
  def global: GlobalState
}

object Context {
  def apply(pageState: PageState, globalState: GlobalState, renderState: PageState => Unit): Context[PageState] =
    new Context[PageState] {
      def local: PageState                  = pageState
      def global: GlobalState               = globalState
      def update(newState: PageState): Unit = renderState(newState)
    }
}
