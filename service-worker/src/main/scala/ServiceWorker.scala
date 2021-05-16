import org.scalajs.dom.experimental.Fetch.fetch
import org.scalajs.dom.experimental.serviceworkers.ServiceWorkerGlobalScope.self
import org.scalajs.dom.experimental.serviceworkers.{ExtendableEvent, FetchEvent}
import org.scalajs.dom.experimental.{RequestInfo, Response}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters._
import scala.util.chaining._

object ServiceWorker {
  private val assetCacheName = "assets"

  private val assets =
    buildinfo.BuildInfo.assetFiles
      .split("\n")
      .filter(_ != "CNAME")
      .map[RequestInfo](fileName => "/" + fileName)
      .toJSArray

  private val startUrl: RequestInfo = "/"

  def main(args: Array[String]): Unit = {
    self.addEventListener(
      "install",
      (event: ExtendableEvent) =>
        (for {
          _ <- invalidateCache(assetCacheName)
          _ <- populateCache(assetCacheName, assets ++ js.Array(startUrl))
          _ = Dynamic.global.console.debug(s"service-worker-build-time: ${buildinfo.BuildInfo.buildTime}")
        } yield ()).toJSPromise
          .tap(event.waitUntil(_))
    )

    self.addEventListener("activate", (_: ExtendableEvent) => self.clients.claim())

    self.addEventListener(
      "fetch",
      (event: FetchEvent) => {
        fromCache(assetCacheName, event.request)
          .recoverWith { case _ => fetch(event.request).toFuture }
          .toJSPromise
          .tap(event.respondWith(_))
      }
    )
  }

  private def populateCache(cacheName: String, files: js.Array[RequestInfo]): Future[Unit] =
    for {
      cache <- self.caches.open(cacheName).toFuture
      _     <- cache.addAll(files).toFuture
    } yield ()

  private def fromCache(cacheName: String, request: RequestInfo): Future[Response] =
    for {
      cache         <- self.caches.open(cacheName).toFuture
      maybeResponse <- cache.`match`(request).toFuture
      response      <- Future(maybeResponse.get)
    } yield response

  private def invalidateCache(cacheName: String): Future[Boolean] =
    self.caches.delete(cacheName).toFuture
}
