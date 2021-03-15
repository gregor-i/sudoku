package frontend

import io.circe.parser
import io.circe.syntax._
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.{PatchFunction, Snabbdom, VNode}

import scala.scalajs.js.{UndefOr, |}

class App(container: Element) {

  var node: Element | VNode = container
  var timeout: Option[Int]  = None

  val patch: PatchFunction = Snabbdom.init(
    classModule = true,
    attributesModule = true,
    styleModule = true,
    eventlistenersModule = true,
    propsModule = true
  )

  private def saveGlobalState(globalState: GlobalState): Unit =
    dom.window.localStorage.setItem("globalState", globalState.asJson.noSpaces)

  private def loadGlobalState(): Option[GlobalState] =
    dom.window.localStorage
      .getItem("globalState")
      .asInstanceOf[UndefOr[String]]
      .toOption
      .flatMap(parser.decode[GlobalState](_).toOption)

  def start(): Unit = {
    val globalState = loadGlobalState().getOrElse(GlobalState.initial)
    renderState(globalState, Router.stateFromUrl(globalState, dom.window.location))
  }

  def renderState(globalState: GlobalState, state: PageState): Unit = {
    saveGlobalState(globalState)

    val context = Context(state, globalState, renderState)

    node = patch(node, Pages.ui(context).toVNode)
  }

  dom.window.onpopstate = _ => start()

  start()
}
