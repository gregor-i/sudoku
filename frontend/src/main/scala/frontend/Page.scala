package frontend

import snabbdom.Node

trait Page[S <: PageState] {
  type State   = S
  type Context = frontend.Context[State]

  def render(using context: Context): Node
}
