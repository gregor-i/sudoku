package frontend

import com.raquo.laminar.api.L.RootNode
import com.raquo.laminar.nodes.ReactiveHtmlElement

trait Page[S <: PageState] {
  type State   = S
  type Context = frontend.Context[State]

  def render(using context: Context): ReactiveHtmlElement[?]
}
