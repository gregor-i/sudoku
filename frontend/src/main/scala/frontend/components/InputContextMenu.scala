package frontend.components

import model.{Dimensions, Position, SudokuBoard}
import org.scalajs.dom.{Element, KeyboardEvent, document}
import snabbdom.{Event, Node}

object InputContextMenu {
  def apply(
      focus: Position,
      dim: Dimensions,
      reference: Element,
      setFocus: Option[Position] => Unit,
      setValue: Option[Int] => Unit
  ): Node = {
    val scale      = 1.1
    val clientRect = reference.getBoundingClientRect()
    val width      = clientRect.width * dim.width * scale
    val height     = clientRect.height * (dim.height + 1) * scale
    Node("div.is-overlay")
      .event[Event]("click", _ => setFocus(None))
      .child {
        InputNumberSVG(dim, interaction = (value, node) => node.event[Event]("click", _ => setValue(value)))
          .styles(
            Seq(
              "position" -> "absolute",
              "left"     -> s"min(calc(100vw - ${width}px), max(0px, ${clientRect.left + (clientRect.width - width) / 2.0}px))",
              "top"      -> s"min(calc(100vh - ${height}px), max(0px, ${clientRect.top + (clientRect.height - height) / 2.0}px))",
              "width"    -> s"${width}px",
              "background" -> "white",
              "box-shadow" -> "2px 2px 3px 4px rgba(0,0,0,0.2)",
              "animation"  -> "fade-in 0.1s linear 1"
            )
          )
      }
      .hookInsert(_ => setupKeyboardHook(focus, dim, setValue, setFocus))
      .hookPostpatch((_, _) => setupKeyboardHook(focus, dim, setValue, setFocus))
      .hookDestroy(_ => destroyKeyboardHook())
  }

  private def setupKeyboardHook(
      focus: Position,
      dim: Dimensions,
      setValue: Option[Int] => Unit,
      setFocus: Some[Position] => Unit
  ): Unit = {
    def rotate(x: Int, y: Int): Some[Position] =
      Some(
        (
          (x + dim.blockSize) % dim.blockSize,
          (y + dim.blockSize) % dim.blockSize
        )
      )

    object ValidNumber {
      def unapply(str: String): Option[Int] = str.toIntOption.filter(SudokuBoard.values(dim).contains)
    }

    val (x, y) = focus

    document.body.onkeydown = (event: KeyboardEvent) => {
      event.key match {
        case "Backspace"    => setValue(None)
        case "Delete"       => setValue(None)
        case ValidNumber(i) => setValue(Some(i))
        case "ArrowUp"      => setFocus(rotate(x, y - 1))
        case "ArrowDown"    => setFocus(rotate(x, y + 1))
        case "ArrowLeft"    => setFocus(rotate(x - 1, y))
        case "ArrowRight"   => setFocus(rotate(x + 1, y))
        case _              => ()
      }
    }
  }

  private def destroyKeyboardHook(): Unit = {
    document.body.onkeydown = _ => ()
  }
}
