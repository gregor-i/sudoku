package frontend.components

import frontend.Context
import frontend.pages.{LandingPageState, SettingsState}
import snabbdom.components.Button
import snabbdom.{Event, Node}

object Header {
  def renderHeader()(implicit context: Context[_]): Node =
    "div.top-bar"
      .child(
        "div"
          .child(
            "div"
              .child("figure.image.is-32x32".child("img".attr("src", Images.logo)))
              .child("span".text("Sudoku"))
              .event[Event]("click", _ => context.update(LandingPageState()))
          )
          .child(
            "div".child(
              Button
                .icon(Icons.settings, _ => context.update(SettingsState()), round = false)
                .classes("is-text")
                .style("text-decoration", "none")
            )
          )
      )
}
