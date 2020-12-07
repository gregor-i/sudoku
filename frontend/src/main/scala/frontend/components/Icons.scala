package frontend.components

import snabbdom.Node

object Icons {
  val solve    = "fa-robot"
  val clear    = "fa-trash"
  val generate = "fa-random"
  val play     = "fa-play"

  val easy   = "fa-ice-cream"
  val medium = "fa-graduation-cap"
  val hard   = "fa-skull-crossbones"

  def icon(icon: String): Node =
    Node("span.icon")
      .child(
        Node("i.fas").`class`(icon)
      )
}
