package frontend

import frontend.language.Language
import snabbdom.Event

trait Syntax {
  inline final def pageState[S <: PageState](using context: Context[S]): S = context.local
  inline final def globalState(using Context[_]): GlobalState              = pageState.globalState
  inline final def localized(using Context[_]): Language                   = pageState.globalState.language

  inline final def setState(pageState: => PageState)(using context: Context[_]): Event => Unit =
    _ => context.update(pageState)

  inline final def action[A <: PageState](action: A => PageState)(using context: Context[A]): Event => Unit =
    _ => context.update(action(context.local))
}
