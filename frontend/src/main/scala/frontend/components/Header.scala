package frontend.components

import frontend.Context
import com.raquo.laminar.api.L.{*, given}

object Header {
  def renderHeader()(using context: Context[_]): Node =
    div(
      cls := "top-bar",
      div(
        div(
          figure(
            cls := "image is-32x32",
            img(src := Images.logo)
          ),
          span("Sudoku")
        ),
        div(
          button(
            cls := "button is-text has-text-primary",
            textDecoration.none,
            i(
              cls := s"fa ${Icons.settings}"
            )
          )
//          Button
//            .icon(Icons.settings, setState(SettingsState(oldGlobalState = globalState)(using globalState)))
//            .classes("is-text", "has-text-primary")
//            .style("text-decoration", "none")
        )
      )
    )
}
