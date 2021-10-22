package frontend.components

import model.Difficulty

object Icons {
  val solve    = "fa-robot"
  val clear    = "fa-trash"
  val generate = "fa-random"
  val play     = "fa-play"
  val settings = "fa-cog"
  val hint     = "fa-question"
//  val infinity = "fa-infinity"
//  <i class="fas fa-circle-notch"></i>

  val continue = "fa-play"
  val easy     = "fa-ice-cream"
  val medium   = "fa-graduation-cap"
  val hard     = "fa-skull-crossbones"

  def difficulty(diff: Difficulty) = diff match {
    case Difficulty.Easy   => easy
    case Difficulty.Medium => medium
    case Difficulty.Hard   => hard
  }
}
