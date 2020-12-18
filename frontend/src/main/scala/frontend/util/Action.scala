package frontend.util

import frontend.{Context, GlobalState, PageState}
import snabbdom.{Event, Snabbdom}

object Action {
  def apply[A <: PageState](action: A => A)(implicit context: Context[A]): Event => Unit =
    _ => context.update(action(context.local))

  def global(action: GlobalState => GlobalState)(implicit context: Context[_]): Event => Unit =
    _ => context.update(action(context.global))
}
