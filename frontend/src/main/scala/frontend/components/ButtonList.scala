package frontend.components

import snabbdom.Node

object ButtonList {
  def apply(buttons: Node*): Node    = Node("div.buttons.is-right").child(buttons)
  def centered(buttons: Node*): Node = Node("div.buttons.is-centered").child(buttons)
}
