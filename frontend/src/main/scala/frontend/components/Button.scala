package frontend.components

import snabbdom.{Event, Eventlistener, Node}

object Button {
  def apply(text: String, icon: String, onclick: Event => Unit): Node =
    Node("button.button")
      .event[Event]("click", onclick)
      .child(Icons.icon(icon))
      .child(Node("span").text(text))

  def apply(text: String, onclick: Event => Unit): Node =
    Node("button.button")
      .event[Event]("click", onclick)
      .text(text)

  def icon(icon: String, onclick: Event => Unit, round: Boolean = true): Node =
    Node("button.button")
      .`class`("is-rounded", round)
      .event[Event]("click", onclick)
      .child(Icons.icon(icon))
}
