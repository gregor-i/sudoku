package serviceWorker

import org.scalajs.dom.Fetch.fetch
import org.scalajs.dom.ServiceWorkerGlobalScope.self
import org.scalajs.dom.*
import org.scalajs.dom

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.Dynamic
import scala.scalajs.js.JSConverters.*
import scala.util.chaining.*

object Main {
  val assetCacheName = "assets"

  val assets: js.Array[dom.RequestInfo] =
    buildinfo.BuildInfo.assetFiles
      .split("\n")
      .filter(_ != "CNAME")
      .map[RequestInfo](fileName => "/" + fileName)
      .toJSArray

  val startUrl: RequestInfo = "/"

  def main(args: Array[String]): Unit = {
    self.addEventListener(
      "install",
      (event: ExtendableEvent) =>
        (for {
          _ <- invalidateCache(assetCacheName)
          _ <- populateCache(assetCacheName, assets)
          _ <- populateCache(assetCacheName, js.Array(startUrl))
          _ = Dynamic.global.console.debug(s"service-worker-build-time: ${buildinfo.BuildInfo.buildTime}")
        } yield ()).toJSPromise
          .tap(event.waitUntil(_))
    )

    self.addEventListener("activate", (_: ExtendableEvent) => self.clients.claim())

    self.addEventListener(
      "fetch",
      (event: FetchEvent) => {
        fromCache(assetCacheName, event.request)
          .recoverWith(_ => fetch(event.request).toFuture)
          .toJSPromise
          .tap(event.respondWith(_))
      }
    )
  }

  private def caches: CacheStorage = self.caches.getOrElse(throw new RuntimeException("caches are undefined ..."))

  def populateCache(cacheName: String, files: js.Array[RequestInfo]): Future[Unit] =
    for {
      cache <- caches.open(cacheName).toFuture
      _     <- cache.addAll(files).toFuture
    } yield ()

  def fromCache(cacheName: String, request: RequestInfo): Future[Response] =
    for {
      cache         <- caches.open(cacheName).toFuture
      maybeResponse <- cache.`match`(request).toFuture
      response      <- Future(maybeResponse.get)
    } yield response

  def invalidateCache(cacheName: String): Future[Boolean] =
    caches.delete(cacheName).toFuture
}
