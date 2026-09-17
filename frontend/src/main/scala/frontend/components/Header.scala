package frontend.components

import frontend.Context
import com.raquo.laminar.api.L.{textToTextNode, *, given}
import frontend.pages.SettingsState

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
          span(
            textToTextNode("Sudoku")
          )
        ),
        div(
          button(
            cls := "button is-text has-text-primary",
            textDecoration.none,
            i(
              cls := s"fa ${Icons.settings}"
            ),
            onClick --> setState(SettingsState(oldGlobalState = globalState)(using globalState))
          )
        )
      )
    )
}
