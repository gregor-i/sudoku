package frontend.components

import model.{Dimensions, SudokuBoard}
import snabbdom.Node

object SudokuBoardSVG {
  type Interaction = ((Int, Int), Node) => Node
  val strokeWidth = (1.0 / 30.0)
  val fontSize    = 0.8.toString

  def apply(board: SudokuBoard[String], interaction: Option[Interaction]): Node = {
    val dim = board.dim

    Node("svg")
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .attr("viewBox", s"${-strokeWidth / 2} ${-strokeWidth / 2} ${dim.blockSize + strokeWidth} ${dim.blockSize + strokeWidth}")
      .childOptional(interaction.map(interactionRects(board, _)))
      .children(grid(dim), values(board))
  }

  private def grid(dim: Dimensions): Node =
    Node("g")
      .attr("id", "grid")
      .style("pointer-events", "none")
      .child {
        for (column <- 0 to dim.blockSize; if column % dim.width != 0)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", dim.blockSize.toString)
            .attr("stroke", "lightgrey")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (row <- 0 to dim.blockSize; if row % dim.height != 0)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "lightgrey")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (column <- 0 to dim.blockSize by dim.width)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", dim.blockSize.toString)
            .attr("stroke", "black")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (row <- 0 to dim.blockSize by dim.height)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "black")
            .attr("stroke-width", strokeWidth.toString)
            .attr("stroke-linecap", "square")
      }

  private def values(board: SudokuBoard[String]): Node =
    Node("g")
      .attr("id", "values")
      .style("pointer-events", "none")
      .child {
        for {
          pos <- SudokuBoard.positions(board.dim)
        } yield Node("text")
          .attr("transform", s"translate(${pos._1} ${pos._2 * 1})")
          .attr("text-anchor", "middle")
          .attr("font-size", fontSize)
          .child(
            Node("tspan")
              .attr("x", "0.5")
              .attr("y", "0.5")
              .attr("alignment-baseline", "central")
              .attr("fill", "currentColor")
              .text(board.get(pos))
          )
      }

  private def interactionRects(board: SudokuBoard[_], interaction: Interaction): Node =
    Node("g")
      .attr("id", "interactionRects")
      .child {
        for {
          pos <- SudokuBoard.positions(board.dim)
        } yield interaction(
          pos,
          Node("rect")
            .attr("id", s"cell_${pos._1}_${pos._2}")
            .attr("x", pos._1.toString)
            .attr("y", pos._2.toString)
            .attr("width", "1")
            .attr("height", "1")
            .attr("fill", "rgba(0, 0, 0, 0)")
            .attr("stroke", "none")
        )
      }
}
