package frontend.components

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.ReactiveHtmlElement

object ButtonList {
  def right(buttons: Modifier[ReactiveHtmlElement.Base]*): ReactiveHtmlElement.Base =
    div(cls := "buttons is-right", buttons)
  def left(buttons: Modifier[ReactiveHtmlElement.Base]*): ReactiveHtmlElement.Base =
    div(cls := "buttons is-left", buttons)
  def centered(buttons: Modifier[ReactiveHtmlElement.Base]*): ReactiveHtmlElement.Base =
    div(cls := "buttons is-centered", buttons)
}
