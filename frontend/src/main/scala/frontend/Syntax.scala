package frontend

import frontend.language.Language

trait Syntax {
  def globalState(implicit context: Context[_]): GlobalState = context.local.globalState
  def localized(implicit context: Context[_]): Language      = globalState.language
}
