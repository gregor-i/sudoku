package frontend.components

import model.{Dimensions, OpenSudokuBoard, SudokuBoard}
import snabbdom.Node

object InputNumberSVG {
  type Interaction = (Int, Node) => Node

  def apply(dim: Dimensions, interaction: Option[Interaction]): Node = {
    Node("svg.number-input")
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .attr("viewBox", s"0 0 ${20 * dim.width} ${20 * dim.height}")
      .childOptional(interaction.map(interactionRects(dim, _)))
      .children(grid(dim), values(dim))
  }

  private def grid(dim: Dimensions): Node =
    Node("g")
      .attr("id", "grid")
      .style("pointer-events", "none")
      .child {
        for (column <- 0 to dim.blockSize)
          yield Node("line")
            .attr("x1", (20 * column).toString)
            .attr("x2", (20 * column).toString)
            .attr("y1", "0")
            .attr("y2", (20 * dim.blockSize).toString)
            .attr("stroke", "black")
      }
      .child {
        for (row <- 0 to dim.blockSize)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", (20 * dim.blockSize).toString)
            .attr("y1", (20 * row).toString)
            .attr("y2", (20 * row).toString)
            .attr("stroke", "black")
      }

  private def values(dim: Dimensions): Node =
    Node("g")
      .attr("id", "values")
      .style("pointer-events", "none")
      .child {
        for {
          x <- 0 until dim.width
          y <- 0 until dim.height
          value = x + y * dim.width + 1
        } yield Node("text")
          .attr("transform", s"translate(${x * 20} ${y * 20})")
          .attr("text-anchor", "middle")
          .child(
            Node("tspan")
              .attr("x", "10")
              .attr("y", "10")
              .attr("alignment-baseline", "central")
              .attr("fill", "currentColor")
              .text(value.toString)
          )
      }

  private def interactionRects(dim: Dimensions, interaction: Interaction): Node =
    Node("g")
      .attr("id", "interationRects")
      .child {
        for {
          x <- 0 until dim.width
          y <- 0 until dim.height
          value = x + y * dim.width + 1
        } yield interaction(
          value,
          Node("rect")
            .attr("x", (x * 20).toString)
            .attr("y", (y * 20).toString)
            .attr("width", "20")
            .attr("height", "20")
            .attr("fill", "rgba(0, 0, 0, 0)")
            .attr("stroke", "rgba(0, 0, 0, 0)")
        )
      }
}
