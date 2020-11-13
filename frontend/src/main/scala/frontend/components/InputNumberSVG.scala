package frontend.components

import model.{Dimensions, OpenSudokuBoard, SudokuBoard}
import snabbdom.Node

object InputNumberSVG {
  type Interaction = (Int, Node) => Node

  val scale    = (1d / 20d).toString
  val fontSize = 0.8.toString

  def apply(dim: Dimensions, interaction: Option[Interaction]): Node = {
    val cell = Math.ceil(Math.sqrt(dim.blockSize)).toInt
    Node("svg.number-input")
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .attr("viewBox", s"0 0 $cell $cell")
      .childOptional(interaction.map(interactionRects(dim, cell, _)))
      .children(grid(cell), values(dim, cell))
  }

  private def grid(cell: Int): Node =
    Node("g")
      .attr("id", "grid")
      .style("pointer-events", "none")
      .child {
        for (column <- 0 to cell)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", cell.toString)
            .attr("stroke", "black")
            .attr("stroke-width", scale)
      }
      .child {
        for (row <- 0 to cell)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", cell.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "black")
            .attr("stroke-width", scale)
      }

  private def values(dim: Dimensions, cell: Int): Node =
    Node("g")
      .attr("id", "values")
      .style("pointer-events", "none")
      .child {
        for {
          x <- 0 until cell
          y <- 0 until cell
          value = x + y * cell
          if value < dim.blockSize
        } yield Node("text")
          .attr("transform", s"translate($x $y)")
          .attr("text-anchor", "middle")
          .attr("font-size", fontSize)
          .child(
            Node("tspan")
              .attr("x", "0.5")
              .attr("y", "0.5")
              .attr("alignment-baseline", "central")
              .attr("fill", "currentColor")
              .text((value + 1).toString)
          )
      }

  private def interactionRects(dim: Dimensions, cell: Int, interaction: Interaction): Node =
    Node("g")
      .attr("id", "interationRects")
      .child {
        for {
          x <- 0 until cell
          y <- 0 until cell
          value = x + y * cell
          if value < dim.blockSize
        } yield interaction(
          value + 1,
          Node("rect")
            .attr("x", x.toString)
            .attr("y", y.toString)
            .attr("width", "1")
            .attr("height", "1")
            .attr("fill", "rgba(0, 0, 0, 0)")
            .attr("stroke", "none")
        )
      }
}
