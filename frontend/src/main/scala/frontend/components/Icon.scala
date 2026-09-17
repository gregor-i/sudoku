package frontend.components

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object Icon {
  def apply(icon: String): ReactiveHtmlElement.Base =
    span(
      cls := "icon",
      i(
        cls := "fas",
        cls := icon
      )
    )
}
