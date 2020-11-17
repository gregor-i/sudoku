package frontend.components

import frontend.components
import model.{Dimensions, OpenSudokuBoard, SolvedSudokuBoard, SudokuBoard}
import snabbdom.Node

object SudokuBoardSVG {
  type Interaction = ((Int, Int), Node) => Node
  val strokeWidth = (1.0 / 20.0).toString
  val fontSize    = 0.8.toString

  def apply[S: DisplayValue](board: SudokuBoard[S], interaction: Option[Interaction]): Node = {
    val dim = board.dim

    Node("svg.sudoku-input")
      .attr("xmlns", "http://www.w3.org/2000/svg")
      .attr("viewBox", s"0 0 ${dim.blockSize} ${dim.blockSize}")
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
            .attr("stroke-width", strokeWidth)
      }
      .child {
        for (row <- 0 to dim.blockSize; if row % dim.height != 0)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "lightgrey")
            .attr("stroke-width", strokeWidth)
      }
      .child {
        for (column <- 0 to dim.blockSize by dim.width)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", dim.blockSize.toString)
            .attr("stroke", "black")
            .attr("stroke-width", strokeWidth)
      }
      .child {
        for (row <- 0 to dim.blockSize by dim.height)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "black")
            .attr("stroke-width", strokeWidth)
      }

  private def values[S: DisplayValue](board: SudokuBoard[S]): Node =
    Node("g")
      .attr("id", "values")
      .style("pointer-events", "none")
      .child {
        for {
          pos   <- SudokuBoard.positions(board.dim)
          value <- implicitly[DisplayValue[S]].apply(board.get(pos))
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
              .text(value)
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
            .attr("x", pos._1.toString)
            .attr("y", pos._2.toString)
            .attr("width", "1")
            .attr("height", "1")
            .attr("fill", "rgba(0, 0, 0, 0)")
            .attr("stroke", "none")
        )
      }
}

trait DisplayValue[S] {
  def apply(s: S): Option[String]
}

object DisplayValue {
  implicit val displayOptionInt: DisplayValue[Option[Int]] = _.map(_.toString)
  implicit val displayInt: DisplayValue[Int]               = s => Some(s.toString)
}
