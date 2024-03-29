package frontend.pages

import frontend.components.Header
import frontend.{GlobalState, Page, PageState}
import snabbdom.Node

case class ErrorState(message: String)(implicit val globalState: GlobalState) extends PageState {
  def setGlobalState(globalState: GlobalState): ErrorState = copy()(globalState = globalState)
}

object ErrorPage extends Page[ErrorState] {
  def render(using context: Context): Node =
    Node("div")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.section")
          .child(
            Node("article.message.is-danger")
              .child(
                Node("div.message-body")
                  .child(Node("div.title").text("An unexpected error occurred."))
                  .child(Node("div.subtitle").text(pageState.message))
                  .child(Node("a").attr("href", "/").text("return to landing page"))
              )
          )
      )
}
