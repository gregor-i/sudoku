package frontend

import frontend.language.Language

trait Syntax {
  export com.raquo.laminar.api.L.{*, given}

  given (using context: Context[?]): GlobalState = context.global

  inline final def pageState[S <: PageState](using context: Context[S]): S = context.local
  inline final def globalState(using Context[?]): GlobalState              = pageState.globalState
  inline final def localized(using Context[?]): Language                   = summon[Context[?]].local.globalState.language

  inline final def setState(pageState: => PageState)(using context: Context[?]): Any => Unit =
    _ => context.update(pageState)

  inline final def action[A <: PageState](action: A => PageState)(using context: Context[A]): Any => Unit =
    _ => context.update(action(context.local))
}

object Syntax extends Syntax
