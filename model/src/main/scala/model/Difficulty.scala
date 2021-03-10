package model

sealed trait Difficulty

object Difficulty {
  case object Easy   extends Difficulty
  case object Medium extends Difficulty
  case object Hard   extends Difficulty
}
