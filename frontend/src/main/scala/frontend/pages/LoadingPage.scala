//package frontend.pages
//
//import frontend.components.Header
//import frontend.{GlobalState, Page, PageState}
//import com.raquo.laminar.api.L.{*, given}
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import scala.util.{Failure, Success}
//
//case class LoadingState(process: Future[PageState])(implicit val globalState: GlobalState) extends PageState {
//  def setGlobalState(globalState: GlobalState): LoadingState = copy()(globalState = globalState)
//}
//
//object LoadingPage extends Page[LoadingState] {
//
//  def render(using context: Context) =
//    div(
//      Header.renderHeader(),
//      i(
//        className := "fa fa-spinner fa-pulse has-text-primary",
//        position.absolute,
//        left       := "50%",
//        right      := "50%",
//        marginLeft := "-5rem",
//        fontSize   := "10rem"
//      )
//    )
////      .hookInsert {
////        _ =>
////          pageState.process.onComplete {
////            case Success(newState) => context.update(newState)
////            case Failure(error) =>
////              context.update(
////                ErrorState(s"unexpected problem while initializing app: ${error.getMessage}")(globalState)
////              )
////          }
////      }
//}
