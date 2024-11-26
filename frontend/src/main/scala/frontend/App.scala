package frontend

import frontend.pages.{LoadingState, PuzzleState}
import io.circe.parser
import io.circe.syntax.*
import org.scalajs.dom
import org.scalajs.dom.Element

import scala.scalajs.js.{UndefOr, |}
import scala.util.Random
import com.raquo.laminar.api.L.{*, given}

class App(container: Element) {

  val globalState: Var[GlobalState] = Var {
    loadGlobalState().getOrElse(GlobalState.initial)
  }

  val pageState: Var[PageState] = Var {
    given GlobalState = globalState.now()
    LoadingState()
  }

  pageState.signal.foreach(println)(using unsafeWindowOwner)

  locally {
    given GlobalState = globalState.now()
    globalState.now().lastPuzzle match {
      case Some(lastPuzzle) =>
        pageState.set(
          PuzzleState.forBoard(lastPuzzle)
        )
      case None =>
        PuzzleState.setAsync(
          seed = Random.nextLong(),
          desiredDifficulty = globalState.now().difficulty,
          dimensions = globalState.now().dimensions,
          storage = pageState
        )
    }
  }

  private def saveGlobalState(globalState: GlobalState): Unit =
    dom.window.localStorage.setItem("globalState", globalState.asJson.noSpaces)

  private def loadGlobalState(): Option[GlobalState] =
    Option(dom.window.localStorage.getItem("globalState"))
      .flatMap(parser.decode[GlobalState](_).toOption)

  locally {
    dom.window.onpopstate = _ => ()

    globalState.signal.foreach(saveGlobalState)(using unsafeWindowOwner)

    val wrapped = div(
      child <-- pageState.signal.map {
        state =>
          val context = Context(state, _ => ())
          Pages.ui(context)
      }
    )

    render(container, wrapped)
  }

}
