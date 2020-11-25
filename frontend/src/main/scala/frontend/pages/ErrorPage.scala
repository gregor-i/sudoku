package frontend.pages

import frontend.Router.Location
import frontend.components.Header
import frontend.{GlobalState, Page, PageState, Router}
import snabbdom.Node

@monocle.macros.Lenses()
case class ErrorState(message: String) extends PageState

object ErrorState {
  def asyncLoadError(error: Throwable) = ErrorState(s"unexpected problem while initializing app: ${error.getMessage}")
}

object ErrorPage extends Page[ErrorState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Router.Path, Router.QueryParameter), PageState] =
    PartialFunction.empty

  override def stateToUrl(state: State): Option[Location] = None

  def render(implicit context: Context): Node =
    Node("div")
      .child(Header.renderHeader())
      .child(
        Node("div.content-column.section")
          .child(
            Node("article.message.is-danger")
              .child(
                Node("div.message-body")
                  .child(Node("div.title").text("An unexpected error occurred."))
                  .child(Node("div.subtitle").text(context.local.message))
                  .child(Node("a").attr("href", "/").text("return to landing page"))
              )
          )
      )
}
