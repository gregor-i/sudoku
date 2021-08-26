package frontend.components

import model.{Dimensions, Position, SudokuBoard}
import org.scalajs.dom.{Element, KeyboardEvent, document}
import snabbdom.{Event, Node}

object InputContextMenu {
  def apply(
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
      .style("z-index", "1")
      .event[Event]("click", _ => setFocus(None))
      .child {
        InputNumberSVG(
          dim,
          interaction = Some { (value, node) =>
            node.event[Event]("click", _ => setValue(value))
          }
        ).styles(
          Seq(
            "position"   -> "absolute",
            "left"       -> s"min(calc(100vw - ${width}px), max(0px, ${clientRect.left + (clientRect.width - width) / 2.0}px))",
            "top"        -> s"min(calc(100vh - ${height}px), max(0px, ${clientRect.top + (clientRect.height - height) / 2.0}px))",
            "width"      -> s"${width}px",
            "background" -> "white",
            "box-shadow" -> "2px 2px 3px 4px rgba(0,0,0,0.2)",
            "animation"  -> "fade-in 0.1s linear 1"
          )
        )
      }
  }

  def globalEventListener(
      dim: Dimensions,
      focus: Option[Position],
      setValue: (Position, Option[Int]) => Unit,
      setFocus: Position => Unit
  )(
      node: Node
  ): Node = {
    def rotate(x: Int, y: Int): (Int, Int) =
      (
        (x + dim.blockSize) % dim.blockSize,
        (y + dim.blockSize) % dim.blockSize
      )

    object ValidNumber {
      def unapply(str: String): Option[Int] = str.toIntOption.filter(SudokuBoard.values(dim).contains)
    }

    def hook() =
      document.body.onkeydown = (event: KeyboardEvent) => {
        (event.key, focus) match {
          case ("Backspace", Some(focus))    => setValue(focus, None)
          case ("Delete", Some(focus))       => setValue(focus, None)
          case (ValidNumber(i), Some(focus)) => setValue(focus, Some(i))
          case ("ArrowUp", Some((x, y)))     => setFocus(rotate(x, y - 1))
          case ("ArrowDown", Some((x, y)))   => setFocus(rotate(x, y + 1))
          case ("ArrowLeft", Some((x, y)))   => setFocus(rotate(x - 1, y))
          case ("ArrowRight", Some((x, y)))  => setFocus(rotate(x + 1, y))
          case _                             => ()
        }
      }

    node
      .hookInsert(_ => hook())
      .hookPostpatch((_, _) => hook())
  }
}
