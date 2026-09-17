package frontend.components

import frontend.components.SudokuBoardSVG.Extension
import model.*
import com.raquo.laminar.api.L.{componentToNode, *, given}
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.modifiers.KeySetter.SvgAttrSetter
import com.raquo.laminar.nodes.ReactiveSvgElement
import org.scalajs.dom.SVGRectElement

import frontend.LaminarHelpers.*

case class SudokuBoardSVG(
    board: SudokuPuzzle,
    extensions: Extension = SudokuBoardSVG.emptyExtension,
    classes: Seq[String] = Nil
) {
  private val strokeWidth  = 1.0 / 30.0
  private val borderRadius = 1d / 10d

  val myOpacity: SvgAttr[String]       = svg.svgAttr("opacity", StringAsIsCodec, None)
  val myPointerEvents: SvgAttr[String] = svg.svgAttr("pointer-events", StringAsIsCodec, None)

  def extendRects(extensions: Extension): SudokuBoardSVG =
    copy(extensions = SudokuBoardSVG.and(this.extensions, extensions))

  def addClasses(classes: String*): SudokuBoardSVG =
    copy(classes = this.classes ++ classes)

  def toNode: Node = {
    val dim = board.dim

    svg.svg(
      svg.cls     := "sudoku-board",
      svg.cls     := classes.mkString(" "),
      svg.xmlns   := "http://www.w3.org/2000/svg",
      svg.viewBox := s"${-strokeWidth / 2} ${-strokeWidth / 2} ${dim.blockSize + strokeWidth} ${dim.blockSize + strokeWidth}",
      grid(),
      rects()
    )
  }

  private def grid(): Node = {
    val dim = board.dim

    svg.g(
      svg.idAttr      := "grid",
      myPointerEvents := "none",
      for (column <- 1 until dim.blockSize)
        yield svg.line(
          svg.x1          := column.toString,
          svg.x2          := column.toString,
          svg.y1          := "0",
          svg.y2          := dim.blockSize.toString,
          svg.stroke      := "currentColor",
          svg.strokeWidth := strokeWidth.toString,
          if column % dim.width != 0 then styleAsStyle(opacity := "0.2")
          else emptyMod
        ),
      for (row <- 1 until dim.blockSize)
        yield svg.line(
          svg.x1          := "0",
          svg.x2          := dim.blockSize.toString,
          svg.y1          := row.toString,
          svg.y2          := row.toString,
          svg.stroke      := "currentColor",
          svg.strokeWidth := strokeWidth.toString,
          if row % dim.height != 0 then styleAsStyle(opacity := "0.2")
          else emptyMod
        ),
      svg.rect(
        svg.x           := "0",
        svg.y           := "0",
        svg.rx          := borderRadius.toString,
        svg.width       := dim.blockSize.toString,
        svg.height      := dim.blockSize.toString,
        svg.fill        := "none",
        svg.stroke      := "black",
        svg.strokeWidth := strokeWidth.toString
      )
    )
  }

  private def rects(): Node =
    svg.g(
      svg.idAttr := "rects",
      for (pos <- SudokuBoard.positions(board.dim))
        yield svg.g(
          svg.transform := s"translate(${pos._1} ${pos._2})",
          svg.rect(
            svg.idAttr := s"cell_${pos._1}_${pos._2}",
            svg.x      := "0",
            svg.y      := "0",
            svg.width  := "1",
            svg.height := "1",
            svg.stroke := "none",
            extensions(pos)
          ),
          numberNode(board.get(pos)).getOrElse(emptyMod)
        )
    )

  private def numberNode(cell: PuzzleCell): Option[Node] = {
    def numberNode(value: Int, `class`: String): Node =
      svg.text(
        svg.textAnchor  := "middle",
        svg.fontSize    := "0.8",
        myPointerEvents := "none",
        svg.tspan(
          svg.cls              := `class`,
          svg.x                := "0.5",
          svg.y                := "0.5",
          svg.dominantBaseline := "central",
          textToTextNode(value)
        )
      )

    cell match {
      case PuzzleCell.Given(value)         => Some(numberNode(value, `class` = "given-value"))
      case PuzzleCell.CorrectInput(value)  => Some(numberNode(value, `class` = "input-value"))
      case PuzzleCell.WrongInput(value, _) => Some(numberNode(value, `class` = "input-value"))
      case PuzzleCell.Empty(_)             => None
    }
  }
}

object SudokuBoardSVG {
  type Extension = Position => Seq[Modifier[ReactiveSvgElement.Base]]

  val emptyExtension: Extension = pos => Seq.empty

  private[SudokuBoardSVG] def and(left: Extension, right: Extension): Extension = pos => left(pos) ++ right(pos)
}
