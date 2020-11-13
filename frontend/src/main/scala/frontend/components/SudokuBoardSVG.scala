package frontend.components

import model.{Dimensions, SudokuBoard}
import model.OpenSudokuBoard
import snabbdom.Node

object SudokuBoardSVG {
  type Interaction = ((Int, Int), Node) => Node

  def apply(board: OpenSudokuBoard, interaction: Option[Interaction]): Node = {
    val dim = board.dim

    Node("svg.sudoku-input")
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .attr("viewBox", s"0 0 ${20 * dim.blockSize} ${20 * dim.blockSize}")
      .childOptional(interaction.map(interactionRects(board, _)))
      .children(grid(dim), values(board))
  }

  private def grid(dim: Dimensions): Node =
    Node("g")
      .attr("id", "grid")
      .child {
        for (column <- 0 to dim.blockSize; if column % dim.width != 0)
          yield Node("line")
            .attr("x1", (20 * column).toString)
            .attr("x2", (20 * column).toString)
            .attr("y1", "0")
            .attr("y2", (20 * dim.blockSize).toString)
            .attr("stroke", "lightgrey")
      }
      .child {
        for (row <- 0 to dim.blockSize; if row % dim.height != 0)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", (20 * dim.blockSize).toString)
            .attr("y1", (20 * row).toString)
            .attr("y2", (20 * row).toString)
            .attr("stroke", "lightgrey")
      }
      .child {
        for (column <- 0 to dim.blockSize by dim.width)
          yield Node("line")
            .attr("x1", (20 * column).toString)
            .attr("x2", (20 * column).toString)
            .attr("y1", "0")
            .attr("y2", (20 * dim.blockSize).toString)
            .attr("stroke", "black")
      }
      .child {
        for (row <- 0 to dim.blockSize by dim.height)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", (20 * dim.blockSize).toString)
            .attr("y1", (20 * row).toString)
            .attr("y2", (20 * row).toString)
            .attr("stroke", "black")
      }

  private def values(board: OpenSudokuBoard): Node =
    Node("g")
      .attr("id", "values")
      .child {
        for {
          pos   <- SudokuBoard.positions(board.dim)
          value <- board.get(pos)
        } yield Node("text")
          .attr("transform", s"translate(${pos._1 * 20} ${pos._2 * 20})")
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

  private def interactionRects(board: OpenSudokuBoard, interaction: Interaction): Node =
    Node("g")
      .attr("id", "interationRects")
      .child {
        for {
          pos <- SudokuBoard.positions(board.dim)
        } yield interaction(
          pos,
          Node("rect")
            .attr("x", (pos._1 * 20).toString)
            .attr("y", (pos._2 * 20).toString)
            .attr("width", "20")
            .attr("height", "20")
            .attr("fill", "rgba(0, 0, 0, 0)")
            .attr("stroke", "rgba(0, 0, 0, 0)")
        )
      }
}
