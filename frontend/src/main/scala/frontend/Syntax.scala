package frontend

import frontend.language.Language

trait Syntax {
  def globalState(using context: Context[_]): GlobalState = context.local.globalState
  def localized(using context: Context[_]): Language      = globalState.language
}
