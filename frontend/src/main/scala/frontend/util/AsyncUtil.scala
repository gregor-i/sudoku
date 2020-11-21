package frontend.util

import org.scalajs.dom

import scala.concurrent.{ExecutionContext, Future, Promise}

object AsyncUtil {
  def sleep(millis: Int): Future[Unit] = {
    val p = Promise[Unit]()
    dom.window.setTimeout(() => p.success(()), millis)
    p.future
  }

  def future[A](op: => A)(implicit ex: ExecutionContext): Future[A] = {
    sleep(0).map(_ => op)
  }
}
