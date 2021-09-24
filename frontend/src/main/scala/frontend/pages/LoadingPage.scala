package frontend.pages

import frontend.Router.Location
import frontend.components.Header
import frontend.{GlobalState, NoRouting, Page, PageState}
import snabbdom.*

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class LoadingState(globalState: GlobalState, process: Future[PageState]) extends PageState {
  def setGlobalState(globalState: GlobalState): LoadingState = copy(globalState = globalState)
}

object LoadingPage extends Page[LoadingState] with NoRouting {

  def render(using context: Context) =
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
      .key(pageState.process.hashCode())
      .hookInsert {
        _ =>
          pageState.process.onComplete {
            case Success(newState) => context.update(newState)
            case Failure(error) =>
              context.update(
                ErrorState(globalState, s"unexpected problem while initializing app: ${error.getMessage}")
              )
          }
      }
}
