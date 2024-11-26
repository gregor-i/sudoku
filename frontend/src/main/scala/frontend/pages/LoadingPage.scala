package frontend.pages

import frontend.{GlobalState, Page, PageState}
import com.raquo.laminar.api.L.{*, given}
import frontend.components.Header

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class LoadingState()(implicit val globalState: GlobalState) extends PageState {
  def setGlobalState(globalState: GlobalState): LoadingState = copy()(globalState = globalState)
}

object LoadingPage extends Page[LoadingState] {

  def render(using context: Context) =
    div(
      Header.renderHeader(),
      i(
        cls := "fa fa-spinner fa-pulse has-text-primary",
        position.absolute,
        left           := "50%",
        top            := "50%",
        marginLeft.rem := -5,
        fontSize.rem   := 10
      )
    )

//    Node("div")
//      .child(Header.renderHeader())
//      .key(pageState.process.hashCode())
//      .hookInsert {
//        _ =>
//          pageState.process.onComplete {
//            case Success(newState) => context.update(newState)
//            case Failure(error) =>
//              context.update(
//                ErrorState(s"unexpected problem while initializing app: ${error.getMessage}")(globalState)
//              )
//          }
//      }
}
