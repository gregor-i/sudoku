package frontend.components

import frontend.Context
import frontend.pages.SettingsState
import snabbdom.components.Button
import snabbdom.{Event, Node}

object Header {
  def renderHeader()(using context: Context[_]): Node =
    "div.top-bar"
      .child(
        "div"
          .child(
            "div"
              .child("figure.image.is-32x32".child("img".attr("src", Images.logo)))
              .child("span".text("Sudoku"))
          )
          .child(
            "div".child(
              Button
                .icon(Icons.settings, setState(SettingsState(oldGlobalState = globalState)(using globalState)))
                .classes("is-text", "has-text-primary")
                .style("text-decoration", "none")
            )
          )
      )
}
