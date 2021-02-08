package frontend

import frontend.language.Language

trait Syntax {
  def localized(implicit context: Context[_]): Language = context.global.language
}
