package frontend

import org.scalajs.dom
import org.scalajs.dom.Event

import scala.scalajs.js

object Main {
  def main(args: Array[String]): Unit =
    dom.document.addEventListener[Event](
      "DOMContentLoaded",
      (_: js.Any) => {
        val container = dom.document.createElement("sudoku-app")
        dom.document.body.appendChild(container)
        new App(container)
      }
    )
}
