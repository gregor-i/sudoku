package frontend.components

import frontend.Context
import frontend.pages.LandingPageState
import snabbdom.{Event, Node}

object Header {
  def renderHeader()(implicit context: Context[_]): Node =
    Node("div.top-bar")
      .child(
        Node("div")
          .child(
            Node("div")
              .child(Node("figure.image.is-32x32").child(Node("img").attr("src", Images.logo)))
              .child(Node("span").text("Sudoku"))
              .event[Event]("click", _ => context.update(LandingPageState()))
          )
      )
}
