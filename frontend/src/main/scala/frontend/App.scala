package frontend

import com.raquo.airstream.state.Var
import com.raquo.laminar.api.L.*
import frontend.PageState.PuzzleState
import io.circe.parser
import io.circe.syntax.*
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.{UndefOr, |}
import scala.util.Random

class App(container: Element) {

  val globalState = Var[GlobalState](loadGlobalState().getOrElse(GlobalState.initial))
  val pageState   = Var[PageState] { PageState.initial(globalState.now()) }

  private def saveGlobalState(globalState: GlobalState): Unit =
    dom.window.localStorage.setItem("globalState", globalState.asJson.noSpaces)

  private def loadGlobalState(): Option[GlobalState] =
    dom.window.localStorage
      .getItem("globalState")
      .asInstanceOf[UndefOr[String]]
      .toOption
      .flatMap(parser.decode[GlobalState](_).toOption)

  def ui = div("Dummy")

  render(container, ui)
}
