package frontend

import com.raquo.laminar.api.L.RootNode

trait Page[S <: PageState] {
  type State   = S
  type Context = frontend.Context[State]

  def render(using context: Context): com.raquo.laminar.nodes.ReactiveHtmlElement[org.scalajs.dom.HTMLDivElement]
}
