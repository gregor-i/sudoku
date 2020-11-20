package frontend.pages

import frontend.Router.{Path, QueryParameter}
import frontend.components.{Button, ButtonList, Header, InputNumberSVG, SudokuBoardSVG}
import frontend.toasts.{ToastType, Toasts}
import frontend.{GlobalState, Page, PageState}
import model.{Dimensions, OpenSudokuBoard, SolvedSudokuBoard, Solver, SudokuBoard, Validate}
import monocle.macros.Lenses
import org.scalajs.dom
import org.scalajs.dom.html.Element
import org.scalajs.dom.raw.ClientRect
import org.scalajs.dom.{KeyboardEvent, document}
import org.w3c.dom.html.HTMLElement
import snabbdom.{Node, Snabbdom}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.util.chaining._

@Lenses
case class SolvedSudokuState(
    board: SolvedSudokuBoard
) extends PageState

object SolvedSudokuPage extends Page[SolvedSudokuState] {
  override def stateFromUrl: PartialFunction[(GlobalState, Path, QueryParameter), PageState] = PartialFunction.empty

  override def stateToUrl(state: State): Option[(Path, QueryParameter)] = None

  override def render(implicit context: Context): Node =
    Node("div.no-scroll")
      .child(Header.renderHeader())
      .child(SudokuBoardSVG(context.local.board.map(_.toString), None).classes("grower"))
}
