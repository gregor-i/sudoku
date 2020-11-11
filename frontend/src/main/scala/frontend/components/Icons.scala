package frontend.components

import snabbdom.Node

object Icons {
  def icon(icon: String): Node =
    Node("span.icon")
      .child(
        Node("i.fas").`class`(icon)
      )
}
