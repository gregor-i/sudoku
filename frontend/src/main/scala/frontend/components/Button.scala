package frontend.components

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.keys.EventProp
import com.raquo.laminar.nodes.ReactiveHtmlElement
import org.scalajs.dom

object Button {
  def apply(text: String, icon: String, clickHandler: dom.MouseEvent => Unit): ReactiveHtmlElement.Base =
   button(
     cls := "button",
     onClick --> clickHandler,
     Icon(icon),
     span(textToTextNode( text))
   )

  def apply(text: String, clickHandler: dom.MouseEvent => Unit): ReactiveHtmlElement.Base =
    button(
      cls := "button",
      onClick --> clickHandler,
      span(textToTextNode(text))
    )


  def icon(icon: String, clickHandler: dom.MouseEvent => Unit, round: Boolean = true): ReactiveHtmlElement.Base =
    button(
      cls := "button",
      if round then cls := "is-round" else emptyMod,
      onClick --> clickHandler,
      Icon(icon),
    )
}