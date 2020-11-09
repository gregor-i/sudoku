package frontend.util

import frontend.{Context, GlobalState, PageState}
import monocle.PLens

trait Updatable[+S, -P] {
  val state: S
  def update(p: P): Unit
}

object Updatable {
  def apply[S, P](paramState: S, paramUpdate: P => Unit): Updatable[S, P] = new Updatable[S, P] {
    override val state: S           = paramState
    override def update(p: P): Unit = paramUpdate(p)
  }

  implicit def fromContext[S <: PageState](implicit context: Context[S]): Updatable[S, PageState] =
    apply(
      context.local,
      context.update
    )

  implicit def fromGlobalContext(implicit context: Context[_]): Updatable[GlobalState, GlobalState] =
    apply(
      context.global,
      context.update
    )

  def composeLens[S, P, A, B](updatable: Updatable[S, P], lens: PLens[S, P, A, B]): Updatable[A, B] =
    Updatable[A, B](
      lens.get(updatable.state),
      a => updatable.update(lens.set(a)(updatable.state))
    )
}
