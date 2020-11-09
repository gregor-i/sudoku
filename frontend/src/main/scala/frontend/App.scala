package frontend

import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.{Snabbdom, SnabbdomFacade, VNode}

import scala.scalajs.js.|

class App(container: Element) {

  var node: Element | VNode = container
  var timeout: Option[Int]  = None

  val patch: SnabbdomFacade.PatchFunction = Snabbdom.init(
    classModule = true,
    attributesModule = true,
    styleModule = true,
    eventlistenersModule = true,
    propsModule = true
  )

  private def saveGlobalState(globalState: GlobalState): Unit = ()

  private def loadGlobalState(): Option[GlobalState] = None

  def start(): Unit = {
    val globalState = loadGlobalState().getOrElse(GlobalState.initial())
    renderState(globalState, Router.stateFromUrl(globalState, dom.window.location))
  }

  def renderState(globalState: GlobalState, state: PageState): Unit = {
    Router.stateToUrl(state) match {
      case Some((currentPath, currentSearch)) =>
        val stringSearch = Router.queryParamsToUrl(currentSearch)
        if (dom.window.location.pathname != currentPath) {
          dom.window.scroll(0, 0)
          dom.window.history.pushState(null, "", currentPath + stringSearch)
        } else {
          dom.window.history.replaceState(null, "", currentPath + stringSearch)
        }
      case None => ()
    }

    saveGlobalState(globalState)

    val context = Context(state, globalState, renderState)

    node = patch(node, Pages.ui(context).toVNode)
  }

  dom.window.onpopstate = _ => start()

  start()
}
