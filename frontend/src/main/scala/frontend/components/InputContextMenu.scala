package frontend.components

import frontend.pages.SudokuSolverPage.Context
import frontend.pages.{Action, SolverState}
import model.{Dimensions, Position}
import org.scalajs.dom.{Element, document}
import snabbdom.{Node, Snabbdom}

object InputContextMenu {
  def apply(
      dim: Dimensions,
      reference: Element,
      setFocus: Option[Position] => Unit,
      setValue: Option[Int] => Unit
  ): Node = {
    val scale      = 2.5
    val clientRect = reference.getBoundingClientRect()
    Node("div.is-overlay")
      .style("background", "rgba(0, 0, 0, 0.2)")
      .style("z-index", "1")
      .event("click", Snabbdom.event(_ => setFocus(None)))
      .child {
        InputNumberSVG(
          dim,
          interaction = Some { (value, node) =>
            node.event("click", Snabbdom.event(_ => setValue(Some(value))))
          }
        ).styles(
          Seq(
            "position"   -> "absolute",
            "left"       -> s"min(calc(100vw - ${clientRect.width * scale}px), max(0px, ${clientRect.left - clientRect.width * (scale - 1.0) / 2.0}px))",
            "top"        -> s"min(calc(100vh - ${clientRect.height * scale}px), max(0px, ${clientRect.top - clientRect.height * (scale - 1.0) / 2.0}px))",
            "width"      -> s"${clientRect.width * scale}px",
            "height"     -> s"${clientRect.height * scale}px",
            "background" -> "white",
            "box-shadow" -> "2px 2px 3px 4px rgba(0,0,0,0.2)"
          )
        )

      }
  }
}
