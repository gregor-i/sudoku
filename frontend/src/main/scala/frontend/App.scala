package frontend

import frontend.pages.PuzzleState
import io.circe.parser
import io.circe.syntax.*
import org.scalajs.dom
import org.scalajs.dom.Element
import snabbdom.{PatchFunction, Snabbdom, VNode}

import scala.scalajs.js.{UndefOr, |}
import scala.util.Random

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

    val pageState = globalState.lastPuzzle match {
      case Some(lastPuzzle) => PuzzleState.forBoard(lastPuzzle)(using globalState)
      case None             =>
        PuzzleState.loading(
          seed = Random.nextLong(),
          desiredDifficulty = globalState.difficulty,
          dimensions = globalState.dimensions
        )(using globalState)
    }

    renderState(pageState)
  }

  def renderState(state: PageState): Unit = {
    saveGlobalState(state.globalState)

    val context = Context(state, renderState)

    node = patch(node, Pages.ui(context).toVNode)
  }

  dom.window.onpopstate = _ => ()

  start()
}
