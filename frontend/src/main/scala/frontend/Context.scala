package frontend

import model.{Difficulty, Dimensions}
import monocle.Lens
import model.DecoratedBoard

sealed trait Context[+S <: PageState] {
  def local: S
  def update(pageState: PageState): Unit
}

object Context {
  def apply(pageState: PageState, renderState: PageState => Unit): Context[PageState] =
    new Context[PageState] {
      def local: PageState                  = pageState
      def update(newState: PageState): Unit = renderState(newState)
    }
}
