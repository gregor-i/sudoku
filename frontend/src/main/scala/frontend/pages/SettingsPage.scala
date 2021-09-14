package frontend.pages

import frontend.components.{Header, Icons, NewPuzzleModal}
import frontend.util.Action
import frontend.{GlobalState, NoRouting, Page, PageState}
import monocle.Lens
import org.scalajs.dom.raw.HTMLSelectElement
import snabbdom.{Event, Node}
import snabbdom.components.{Button, Modal}

case class SettingsState(modalOpened: Boolean = false) extends PageState

object SettingsState {
  val modalOpened = Lens[SettingsState, Boolean](_.modalOpened)(s => _.copy(modalOpened = s))
}

object SettingsPage extends Page[SettingsState] with NoRouting {

  override def render(implicit context: Context): Node =
    "div.grid-layout"
      .key("SettingsPage")
      .child(Header.renderHeader())
      .child(
        "div.grid-main.my-2"
          .children(
            "h1.title".text(localized.settings),
            assistance()
          )
      )
      .child(
        "div.grid-footer.my-2.buttons"
          .child(
            Button(localized.playSudoku, Icons.play, Action(SettingsState.modalOpened.replace(true)))
              .classes("is-fullwidth", "is-primary")
          )
      )
      .maybeModify(context.local.modalOpened) {
        _.child(
          Modal(closeAction = Some(Action(SettingsState.modalOpened.replace(false))))(
            NewPuzzleModal(context.global.lastPuzzle)
          )
        )
      }

  def assistance()(implicit context: Context): Node =
    selectInput[Boolean](
      label = localized.highlightMistakes,
      options = Seq(
        localized.yes -> true,
        localized.no  -> false
      ),
      lens = GlobalState.highlightMistakes,
      eqFunction = (a, b) => a == b
    )

  def selectInput[A](
      label: String,
      options: Seq[(String, A)],
      lens: Lens[GlobalState, A],
      eqFunction: (A, A) => Boolean
  )(implicit
      context: Context
  ) = {
    val currentValue = lens.get(context.global)

    "div.field"
      .child(
        "label.label".text(label)
      )
      .child(
        "div.control.is-expanded"
          .child(
            "div.select.is-full-width"
              .child(
                "select"
                  .event[Event](
                    "change",
                    event => {
                      val selected = event.target.asInstanceOf[HTMLSelectElement].value
                      options
                        .find(_._1 == selected)
                        .map(_._2)
                        .foreach(input => context.update(lens.replace(input)(context.global)))
                    }
                  )
                  .child(
                    options.map {
                      case (stringValue, value) =>
                        "option"
                          .boolAttr("selected", eqFunction(value, currentValue))
                          .text(stringValue)
                    }
                  )
              )
          )
      )
  }

}
