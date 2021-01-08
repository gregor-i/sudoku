package frontend.components

import model._
import snabbdom.Node

object SudokuBoardSVG {
  type Interaction = (Position, Node) => Node
  val strokeWidth = (1.0 / 30.0)

  def apply(board: DecoratedBoard, interaction: Option[Interaction]): Node = {
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
            .attr("stroke", "currentColor")
            .style("opacity", "0.2")
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
            .attr("stroke", "currentColor")
            .style("opacity", "0.2")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (column <- 0 to dim.blockSize by dim.width)
          yield Node("line")
            .attr("x1", column.toString)
            .attr("x2", column.toString)
            .attr("y1", "0")
            .attr("y2", dim.blockSize.toString)
            .attr("stroke", "currentColor")
            .attr("stroke-width", strokeWidth.toString)
      }
      .child {
        for (row <- 0 to dim.blockSize by dim.height)
          yield Node("line")
            .attr("x1", "0")
            .attr("x2", dim.blockSize.toString)
            .attr("y1", row.toString)
            .attr("y2", row.toString)
            .attr("stroke", "currentColor")
            .attr("stroke-width", strokeWidth.toString)
            .attr("stroke-linecap", "square")
      }

  private def values(board: DecoratedBoard): Node =
    Node("g")
      .attr("id", "values")
      .style("pointer-events", "none")
      .child {
        for {
          pos <- SudokuBoard.positions(board.dim)
          node <- board.get(pos) match {
            case cell: DecoratedCell.Given      => Some(givenNumber(cell))
            case cell: DecoratedCell.Input      => Some(inputNumber(cell))
            case cell: DecoratedCell.WrongInput => Some(wrongInputNumber(cell))
            case DecoratedCell.Empty            => None
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
    numberPrototype.text(state.value.toString).style("font-weight", "300")

  private def inputNumber(state: DecoratedCell.Input): Node =
    numberPrototype.text(state.value.toString)

  private def wrongInputNumber(state: DecoratedCell.WrongInput): Node =
    numberPrototype.text(state.value.toString).style("color", "red")

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
