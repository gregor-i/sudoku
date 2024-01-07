//package frontend.components
//
//import model.Dimensions
//import snabbdom.Node
//
//object InputNumberSVG {
//  type Interaction = (Option[Int], Node) => Node
//
//  private val strokeWidth  = 1d / 30d
//  private val borderRadius = 1d / 10d
//  private val fontSize     = 0.8.toString
//
//  def apply(dim: Dimensions, interaction: Interaction): Node =
//    Node("svg.number-input")
//      .attr("xmlns", "http://www.w3.org/2000/svg")
//      .attr("viewBox", s"${-strokeWidth / 2} ${-strokeWidth / 2} ${dim.width + strokeWidth} ${dim.height + 1 + strokeWidth}")
//      .style("border-radius", s"${borderRadius / (dim.width + strokeWidth) * 100d}%")
//      .children(grid(dim), values(dim), interactionRects(dim, interaction))
//
//  private def grid(dim: Dimensions): Node =
//    Node("g")
//      .attr("id", "grid")
//      .style("pointer-events", "none")
//      .child {
//        for (column <- 1 until dim.width)
//          yield Node("line")
//            .attr("x1", column.toString)
//            .attr("x2", column.toString)
//            .attr("y1", "0")
//            .attr("y2", dim.height.toString)
//            .attr("stroke", "black")
//            .attr("stroke-width", strokeWidth.toString)
//            .style("opacity", "0.2")
//      }
//      .child {
//        for (row <- 1 to dim.height)
//          yield Node("line")
//            .attr("x1", "0")
//            .attr("x2", dim.width.toString)
//            .attr("y1", row.toString)
//            .attr("y2", row.toString)
//            .attr("stroke", "black")
//            .attr("stroke-width", strokeWidth.toString)
//            .attr("stroke-linecap", "square")
//            .style("opacity", "0.2")
//      }
//      .child(
//        Node("rect")
//          .attr("x", "0")
//          .attr("y", "0")
//          .attr("rx", borderRadius.toString)
//          .attr("width", dim.width.toString)
//          .attr("height", (dim.height + 1).toString)
//          .attr("fill", "none")
//          .attr("stroke", "black")
//          .attr("stroke-width", strokeWidth.toString)
//      )
//  private def values(dim: Dimensions): Node =
//    Node("g")
//      .attr("id", "values")
//      .style("pointer-events", "none")
//      .child {
//        for {
//          x <- 0 until dim.width
//          y <- 0 until dim.height
//          value = x + y * dim.width
//          if value < dim.blockSize
//        } yield Node("text")
//          .attr("transform", s"translate($x $y)")
//          .attr("text-anchor", "middle")
//          .attr("font-size", fontSize)
//          .child(
//            Node("tspan")
//              .attr("x", "0.5")
//              .attr("y", "0.5")
//              .attr("dominant-baseline", "central")
//              .attr("fill", "currentColor")
//              .text((value + 1).toString)
//          )
//      }
//      .child(
//        Node("image")
//          .attr("x", (dim.width / 2.0 - 0.5).toString)
//          .attr("y", dim.height.toString)
//          .attr("width", "1")
//          .attr("height", "1")
//          .attr("href", "/trash.svg")
//          .attr("transform", "scale(0.6)")
//          .style("transform-box", "fill-box")
//          .style("transform-origin", "center")
//      )
//
//  private def interactionRects(dim: Dimensions, interaction: Interaction): Node =
//    Node("g")
//      .attr("id", "interactionRects")
//      .child {
//        for {
//          x <- 0 until dim.width
//          y <- 0 until dim.height
//          value = x + y * dim.width
//        } yield interaction(
//          Some(value + 1),
//          Node("rect")
//            .attr("x", x.toString)
//            .attr("y", y.toString)
//            .attr("width", "1")
//            .attr("height", "1")
//            .attr("fill", "rgba(0, 0, 0, 0)")
//            .attr("stroke", "none")
//        )
//      }
//      .child {
//        interaction(
//          None,
//          Node("rect")
//            .attr("x", "0")
//            .attr("y", dim.height.toString)
//            .attr("width", dim.width.toString)
//            .attr("height", "1")
//            .attr("fill", "rgba(0, 0, 0, 0)")
//            .attr("stroke", "none")
//        )
//      }
//}
