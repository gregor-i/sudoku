package frontend.components

import model.Dimensions
import com.raquo.laminar.api.L.{componentToNode, *, given}
import com.raquo.laminar.api.L
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.nodes.{ReactiveHtmlElement, ReactiveSvgElement}
import org.scalajs.dom.SVGSVGElement

import frontend.LaminarHelpers.*

object InputNumberSVG {
  type Interaction = (Option[Int]) => Unit

  private val strokeWidth  = 1d / 30d
  private val borderRadius = 1d / 10d
  private val fontSize     = 0.8.toString

  def apply(
      dim: Dimensions,
      interaction: Interaction,
      modifier: Modifier[ReactiveSvgElement[SVGSVGElement]]
  ): ReactiveSvgElement[?] =
    svg.svg(
      svg.xmlns   := "http://www.w3.org/2000/svg",
      svg.viewBox := s"${-strokeWidth / 2} ${-strokeWidth / 2} ${dim.width + strokeWidth} ${dim.height + 1 + strokeWidth}",
      styleAsStyle(L.borderRadius := s"${borderRadius / (dim.width + strokeWidth) * 100d}%"),
      grid(dim),
      values(dim),
      interactionRects(dim, interaction),
      modifier
    )

  private def grid(dim: Dimensions): Node =
    svg.g(
      svg.idAttr := "grid",
      styleAsStyle(pointerEvents.none),
      seqToModifier {
        for (column <- 1 until dim.width)
          yield svg.line(
            svg.x1          := column.toString,
            svg.x2          := column.toString,
            svg.y1          := "0",
            svg.y2          := dim.height.toString,
            svg.stroke      := "black",
            svg.strokeWidth := strokeWidth.toString,
            styleAsStyle(opacity := "0.2")
          )
      },
      seqToModifier {
        for (row <- 1 to dim.height)
          yield svg.line(
            svg.x1            := "0",
            svg.x2            := dim.width.toString,
            svg.y1            := row.toString,
            svg.y2            := row.toString,
            svg.stroke        := "black",
            svg.strokeWidth   := strokeWidth.toString,
            svg.strokeLineCap := "square",
            styleAsStyle(opacity := "0.2")
          )
      },
      svg.rect(
        svg.x           := "0",
        svg.y           := "0",
        svg.rx          := borderRadius.toString,
        svg.width       := dim.width.toString,
        svg.height      := (dim.height + 1).toString,
        svg.fill        := "none",
        svg.stroke      := "black",
        svg.strokeWidth := strokeWidth.toString
      )
    )

  private def values(dim: Dimensions): Node =
    svg.g(
      svg.idAttr := "values",
      styleAsStyle(pointerEvents.none),
      seqToModifier {
        for {
          x <- 0 until dim.width
          y <- 0 until dim.height
          value = x + y * dim.width
          if value < dim.blockSize
        } yield svg.text(
          svg.transform  := s"translate($x $y)",
          svg.textAnchor := "middle",
          svg.fontSize   := fontSize,
          svg.tspan(
            svg.x                := "0.5",
            svg.y                := "0.5",
            svg.dominantBaseline := "central",
            svg.fill             := "currentColor",
            textToTextNode((value + 1).toString)
          )
        )
      },
      svg.image(
        svg.x         := (dim.width / 2.0 - 0.5).toString,
        svg.y         := dim.height.toString,
        svg.width     := "1",
        svg.height    := "1",
        svg.href      := "/trash.svg",
        svg.transform := "scale(0.6)",
        styleAsStyle(styleProp[String]("transform-box") := "fill-box"),
        styleAsStyle(transformOrigin                    := "center")
      )
    )

  private def interactionRects(dim: Dimensions, interaction: Interaction): Node =
    svg.g(
      svg.idAttr := "interactionRects",
      seqToModifier {
        for {
          x <- 0 until dim.width
          y <- 0 until dim.height
          value = x + y * dim.width
        } yield svg.rect(
          svg.x      := x.toString,
          svg.y      := y.toString,
          svg.width  := "1",
          svg.height := "1",
          svg.fill   := "rgba(0, 0, 0, 0)",
          svg.stroke := "none",
          onClick --> (_ => interaction(Some(value + 1)))
        )
      },
      svg.rect(
        svg.x      := "0",
        svg.y      := dim.height.toString,
        svg.width  := dim.width.toString,
        svg.height := "1",
        svg.fill   := "rgba(0, 0, 0, 0)",
        svg.stroke := "none",
        onClick --> (_ => interaction(None))
      )
    )
}
