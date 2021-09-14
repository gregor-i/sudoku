package frontend.pages

import frontend.Router.Location
import frontend.components.Header
import frontend.{Page, PageState}
import snabbdom._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class LoadingState(process: Future[PageState]) extends PageState

object LoadingPage extends Page[LoadingState] {
  def stateFromUrl = PartialFunction.empty

  def stateToUrl(state: State): Option[Location] = None

  def render(implicit context: Context) =
    Node("div")
      .child(Header.renderHeader())
      .child(
        Node("i.fa.fa-spinner.fa-pulse.has-text-primary")
          .styles(
            Seq(
              "position"   -> "absolute",
              "left"       -> "50%",
              "top"        -> "50%",
              "marginLeft" -> "-5rem",
              "fontSize"   -> "10rem"
            )
          )
      )
      .key(context.local.process.hashCode())
      .hookInsert {
        _ =>
          context.local.process.onComplete {
            case Success(newState) => context.update(newState)
            case Failure(error)    => context.update(ErrorState.asyncLoadError(error))
          }
      }
}
