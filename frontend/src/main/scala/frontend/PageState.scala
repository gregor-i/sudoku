package frontend

import monocle.Lens

trait PageState {
  def globalState: GlobalState
  def setGlobalState(globalState: GlobalState): PageState
}

object PageState {
  def globalState: Lens[PageState, GlobalState] =
    Lens[PageState, GlobalState](get = _.globalState)(replace = s => t => t.setGlobalState(s))
}
