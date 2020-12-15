package frontend

import snabbdom.Node

import scala.reflect.ClassTag

abstract class Page[S <: PageState: ClassTag] {
  type State   = S
  type Context = frontend.Context[State]

  def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState]

  def stateToUrl(state: State): Option[Router.Location]

  def render(implicit context: Context): Node

  def acceptState(nutriaState: PageState): Boolean = implicitly[ClassTag[State]].runtimeClass == nutriaState.getClass
}

trait NoRouting { _: Page[_] =>
  def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState] =
    PartialFunction.empty

  def stateToUrl(state: State): Option[Router.Location] = None
}
