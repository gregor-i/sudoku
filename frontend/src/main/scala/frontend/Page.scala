package frontend

import snabbdom.Node

trait Page[S <: PageState] {
  type State   = S
  type Context = frontend.Context[State]

  def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState]

  def stateToUrl: PartialFunction[PageState, Router.Location]

  def render(using context: Context): Node
}

trait NoRouting { self: Page[_] =>
  override def stateFromUrl = PartialFunction.empty
  override def stateToUrl   = PartialFunction.empty
}
