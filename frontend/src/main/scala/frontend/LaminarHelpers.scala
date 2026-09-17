package frontend

import com.raquo.laminar.DomApi
import com.raquo.laminar.api.L.{StyleProp, SvgAttr, svg}
import com.raquo.laminar.codecs.StringAsIsCodec
import com.raquo.laminar.keys.StyleProp
import com.raquo.laminar.modifiers.KeySetter
import com.raquo.laminar.modifiers.KeySetter.{StyleSetter, SvgAttrSetter}
import com.raquo.laminar.nodes.{ReactiveHtmlElement, ReactiveSvgElement}

import scala.scalajs.js

object LaminarHelpers {

  def styleAsAttribute(style: StyleProp[String]): SvgAttr[String] = svg.svgAttr(style.name, StringAsIsCodec, None)
  def styleAsAttribute(style: StyleSetter): SvgAttrSetter[String] =
    svg.svgAttr(style.key.name, StringAsIsCodec, None) := style.value

  def styleAsStyle(style: StyleSetter): KeySetter[StyleProp[?], String, ReactiveSvgElement.Base] =
    new KeySetter(
      key = style.key,
      value = style.value,
      action = (el, key, value) => {
        el.ref.asInstanceOf[js.Dynamic].style.setProperty(key.name, value)
      }
    )
}
