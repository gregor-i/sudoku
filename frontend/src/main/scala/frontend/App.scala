package frontend

import frontend.pages.PuzzleState
import io.circe.parser
import io.circe.syntax._
import io.circe.generic.auto._
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.{Snabbdom, SnabbdomFacade, VNode}

import scala.scalajs.js.{UndefOr, |}

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

  private def saveGlobalState(globalState: GlobalState): Unit =
    dom.window.localStorage.setItem("globalState", globalState.asJson.noSpaces)

  private def loadGlobalState(): Option[GlobalState] =
    dom.window.localStorage
      .getItem("globalState")
      .asInstanceOf[UndefOr[String]]
      .toOption
      .flatMap(parser.decode[GlobalState](_).toOption)

  def start(): Unit = {
    val globalState = loadGlobalState().getOrElse(GlobalState.initial())
    renderState(globalState, Router.stateFromUrl(globalState, dom.window.location))
  }

  def renderState(globalState: GlobalState, state: PageState): Unit = {
    val modifiedGlobalState = state match {
      case puzzle: PuzzleState => globalState.copy(lastPuzzle = Some(puzzle.copy(focus = None)))
      case _                   => globalState
    }

    saveGlobalState(modifiedGlobalState)

    val context = Context(state, modifiedGlobalState, renderState)

    node = patch(node, Pages.ui(context).toVNode)
  }

  dom.window.onpopstate = _ => start()

  start()
}
