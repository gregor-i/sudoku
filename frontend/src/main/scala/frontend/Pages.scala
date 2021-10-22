package frontend

import frontend.pages._
import snabbdom.Node

object Pages {
  val all: Seq[Page[_]] = Seq(
    ErrorPage,
    LoadingPage,
    PuzzlePage,
    FinishedPuzzlePage,
    SettingsPage
  )

  def ui(context: Context[PageState]): Node = context.local match {
    case _: ErrorPage.State          => ErrorPage.render(using context.asInstanceOf)
    case _: LoadingPage.State        => LoadingPage.render(using context.asInstanceOf)
    case _: PuzzlePage.State         => PuzzlePage.render(using context.asInstanceOf)
    case _: FinishedPuzzlePage.State => FinishedPuzzlePage.render(using context.asInstanceOf)
    case _: SettingsPage.State       => SettingsPage.render(using context.asInstanceOf)
  }
}
