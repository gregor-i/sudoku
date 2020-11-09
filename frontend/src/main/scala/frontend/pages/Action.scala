package frontend.pages

import frontend.{Context, GlobalState, PageState}
import snabbdom.Snabbdom

object Action {
  def apply[A <: PageState](action: A => A)(implicit context: Context[A]) =
    Snabbdom.event(_ => context.update(action(context.local)))

  def global(action: GlobalState => GlobalState)(implicit context: Context[_]) =
    Snabbdom.event(_ => context.update(action(context.global)))
}
