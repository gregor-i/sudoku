//package frontend.components
//
//import frontend.components.SudokuBoardSVG.Extension
//import model._
//import snabbdom.Node
//
//case class SudokuBoardSVG(board: SudokuPuzzle, extension: Extension = SudokuBoardSVG.emptyExtension) {
//  private val strokeWidth  = 1.0 / 30.0
//  private val borderRadius = 1d / 10d
//
//  def extendRects(extension: Extension): SudokuBoardSVG =
//    copy(extension = SudokuBoardSVG.and(this.extension, extension))
//
//  def toNode: Node = {
//    val dim = board.dim
//
//    Node("svg.sudoku-board")
//      .attr("xmlns", "http://www.w3.org/2000/svg")
//      .attr("viewBox", s"${-strokeWidth / 2} ${-strokeWidth / 2} ${dim.blockSize + strokeWidth} ${dim.blockSize + strokeWidth}")
//      .children(rects(), grid())
//  }
//
//  private def grid(): Node = {
//    val dim = board.dim
//
//    Node("g")
//      .attr("id", "grid")
//      .style("pointer-events", "none")
//      .child {
//        for (column <- 1 until dim.blockSize)
//          yield Node("line")
//            .attr("x1", column.toString)
//            .attr("x2", column.toString)
//            .attr("y1", "0")
//            .attr("y2", dim.blockSize.toString)
//            .attr("stroke", "currentColor")
//            .attr("stroke-width", strokeWidth.toString)
//            .maybeModify(column % dim.width != 0)(_.style("opacity", "0.2"))
//      }
//      .child {
//        for (row <- 1 until dim.blockSize)
//          yield Node("line")
//            .attr("x1", "0")
//            .attr("x2", dim.blockSize.toString)
//            .attr("y1", row.toString)
//            .attr("y2", row.toString)
//            .attr("stroke", "currentColor")
//            .attr("stroke-width", strokeWidth.toString)
//            .maybeModify(row % dim.height != 0)(_.style("opacity", "0.2"))
//      }
//      .child(
//        Node("rect")
//          .attr("x", "0")
//          .attr("y", "0")
//          .attr("rx", borderRadius.toString)
//          .attr("width", dim.blockSize.toString)
//          .attr("height", dim.blockSize.toString)
//          .attr("fill", "none")
//          .attr("stroke", "black")
//          .attr("stroke-width", strokeWidth.toString)
//      )
//  }
//
//  private def numberNode(cell: PuzzleCell): Option[Node] = {
//    def numberNode(value: Int, `class`: String): Node =
//      Node("text")
//        .attr("text-anchor", "middle")
//        .attr("font-size", "0.8")
//        .style("pointer-events", "none")
//        .child {
//          Node("tspan")
//            .classes(`class`)
//            .attr("x", "0.5")
//            .attr("y", "0.5")
//            .attr("dominant-baseline", "central")
//            .text(value.toString)
//        }
//
//    cell match {
//      case PuzzleCell.Given(value)         => Some(numberNode(value, `class` = "given-value"))
//      case PuzzleCell.CorrectInput(value)  => Some(numberNode(value, `class` = "input-value"))
//      case PuzzleCell.WrongInput(value, _) => Some(numberNode(value, `class` = "input-value"))
//      case PuzzleCell.Empty(_)             => None
//    }
//  }
//
//  private def rects(): Node =
//    Node("g")
//      .attr("id", "rects")
//      .child {
//        for (pos <- SudokuBoard.positions(board.dim))
//          yield {
//            Node("g")
//              .attr("transform", s"translate(${pos._1} ${pos._2})")
//              .child(
//                Node("rect")
//                  .attr("id", s"cell_${pos._1}_${pos._2}")
//                  .attr("x", "0")
//                  .attr("y", "0")
//                  .attr("width", "1")
//                  .attr("height", "1")
//                  .attr("stroke", "none")
//                  .modify(extension(pos, _))
//              )
//              .childOptional(numberNode(board.get(pos)))
//          }
//      }
//}
//
//object SudokuBoardSVG {
//  type Extension = (Position, Node) => Node
//
//  val emptyExtension: Extension = (pos, node) => node
//
//  private[SudokuBoardSVG] def and(left: Extension, right: Extension): Extension = (pos, node) => right(pos, left(pos, node))
//}
