package frontend.components

import snabbdom.{Event, Node}

object Modal {
  def apply(closeAction: Event => Unit)(content: Node*): Node =
    Node("div.modal.is-active")
      .child(Node("div.modal-background").event("click", closeAction))
      .child(Node("div.modal-content").child(Node("div.box").child(content)))
}
