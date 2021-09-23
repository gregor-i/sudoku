package frontend.util

import frontend.{Context, GlobalState, PageState}
import snabbdom.Event

object Action {
  def apply[A <: PageState](action: A => A)(implicit context: Context[A]): Event => Unit =
    _ => context.update(action(context.local))
}
