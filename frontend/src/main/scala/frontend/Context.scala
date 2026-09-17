package frontend

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.Var
import model.{Difficulty, Dimensions}
import monocle.Lens

sealed trait Context[+S <: PageState] {
  def local: S
  def update(pageState: PageState): Unit
  def global: GlobalState

  def localVar: Var[PageState]
}

object Context {
  def apply(
      pageState: PageState,
      globalState: GlobalState,
      renderState: PageState => Unit,
      _localVar: Var[PageState]
  ): Context[PageState] =
    new Context[PageState] {
      def local: PageState                  = pageState
      def global: GlobalState               = globalState
      def update(newState: PageState): Unit = renderState(newState)

      def localVar: Var[PageState] = _localVar
    }
}
