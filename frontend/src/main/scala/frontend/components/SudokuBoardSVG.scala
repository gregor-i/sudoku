package frontend.components

import model._
import snabbdom.Node

object SudokuBoardSVG {
  type Extension = (Position, Node) => Node
  val strokeWidth  = (1.0 / 30.0)
  val borderRadius = 1d / 10d

  def apply(board: DecoratedBoard, extension: Option[Extension], highlightMistakes: Boolean): Node = {
    val dim = board.dim

    Node("svg.sudoku-board")
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .attr("viewBox", s"${-strokeWidth / 2} ${-strokeWidth / 2} ${dim.blockSize + strokeWidth} ${dim.blockSize + strokeWidth}")
      .child(rects(board, extension, highlightMistakes))
      .children(grid(dim), values(board))
  }

  private def grid(dim: Dimensions): Node =
    Node("g")
      .attr("id", "grid")
      .style("pointer-events", "none")
      .child {
        for (column <- 1 until dim.blockSize; if column % dim.width != 0)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", dim.blockSize.toString)
            .attr("stroke", "currentColor")
            .style("opacity", "0.2")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (row <- 1 until dim.blockSize; if row % dim.height != 0)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "currentColor")
            .style("opacity", "0.2")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (column <- dim.width until dim.blockSize by dim.width)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", dim.blockSize.toString)
            .attr("stroke", "currentColor")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (row <- dim.height until dim.blockSize by dim.height)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "currentColor")
            .attr("stroke-width", strokeWidth.toString)
            .attr("stroke-linecap", "square")
      }
      .child(
        Node("rect")
          .attr("x", "0")
          .attr("y", "0")
          .attr("rx", borderRadius.toString)
          .attr("width", dim.blockSize.toString)
          .attr("height", dim.blockSize.toString)
          .attr("fill", "none")
          .attr("stroke", "black")
          .attr("stroke-width", strokeWidth.toString)
      )

  private def values(board: DecoratedBoard): Node =
    Node("g")
      .attr("id", "values")
      .style("pointer-events", "none")
      .child {
        for {
          pos <- SudokuBoard.positions(board.dim)
          node <- board.get(pos) match {
            case cell: DecoratedCell.Given => Some(givenNumber(cell))
            case cell: DecoratedCell.Input => Some(inputNumber(cell))
            case DecoratedCell.Empty       => None
          }
        } yield Node("text")
          .attr("transform", s"translate(${pos._1} ${pos._2 * 1})")
          .attr("text-anchor", "middle")
          .attr("font-size", "0.8")
          .child(node)
      }

  private val numberPrototype =
    Node("tspan")
      .attr("x", "0.5")
      .attr("y", "0.5")
      .attr("dominant-baseline", "central")

  private def givenNumber(state: DecoratedCell.Given): Node =
    numberPrototype.text(state.value.toString).classes("given-value")

  private def inputNumber(state: DecoratedCell.Input): Node =
    numberPrototype.text(state.value.toString).classes("input-value")

  private def rects(board: DecoratedBoard, interaction: Option[Extension], highlightMistakes: Boolean): Node =
    Node("g")
      .attr("id", "rects")
      .child {
        for (pos <- SudokuBoard.positions(board.dim))
          yield {
            val node = Node("rect")
              .`class`("wrong-value", highlightMistakes && !Validate.noError(board.map(_.toOption), pos))
              .attr("id", s"cell_${pos._1}_${pos._2}")
              .attr("x", pos._1.toString)
              .attr("y", pos._2.toString)
              .attr("width", "1")
              .attr("height", "1")
              .attr("stroke", "none")

            interaction match {
              case Some(interaction) => interaction(pos, node)
              case None              => node
            }
          }
      }
}
