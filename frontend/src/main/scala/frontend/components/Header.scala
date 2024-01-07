//package frontend.components
//
//import frontend.Context
////import frontend.pages.SettingsState
//import com.raquo.laminar.api.L.{*, given}
//
//object Header {
//  def renderHeader()(using context: Context[_]) =
//    div(
//      className := "top-bar",
//      div(
//        div(
//          figure(
//            className := "image is-32x32",
//            img(src := Images.logo)
//          ),
//          span("Sudoku")
//        ),
//        div(
//          button(
//            className := "button",
//            onClick := ???,
//            
//          )
//          Button
//            .icon(Icons.settings, setState(SettingsState(oldGlobalState = globalState)(using globalState)))
//            .classes("is-text", "has-text-primary")
//            .style("text-decoration", "none")
//        )
//      )
//    )
//
//  def apply(text: String, onclick: Event => Unit): Node =
//    Node("button.button")
//      .event[Event]("click", onclick)
//      .text(text)
//}
