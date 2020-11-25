package frontend.components

import snabbdom.Node

object Icons {
  val solve    = "fa-robot"
  val clear    = "fa-trash"
  val generate = "fa-random"
  val play     = "fa-play"

  def icon(icon: String): Node =
    Node("span.icon")
      .child(
        Node("i.fas").`class`(icon)
      )
}
