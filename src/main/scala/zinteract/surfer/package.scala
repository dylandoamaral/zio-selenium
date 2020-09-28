package zinteract

import zio.{Task, IO, Has, RIO, ZIO, ZLayer}
import zinteract.webdriver.WebDriver
import zio.UIO

package object surfer {
  type Surfer = Has[Surfer.Service]

  object Surfer extends Serializable {
    trait Service extends Serializable {
      def link(url: String): IO[FailLinkError, Unit]

      def url(): UIO[String]
    }

    object Service {
      val live: ZLayer[WebDriver, Nothing, Surfer] =
        ZLayer.fromService(webdriver =>
          new Surfer.Service {
            def link(url: String): IO[FailLinkError, Unit] =
              ZIO.effect(webdriver.get(url)).mapError(_ => FailLinkError(url))

            def url(): UIO[String] =
              ZIO.effect(webdriver.getCurrentUrl).orElse(ZIO.succeed("about:blank"))
          }
        )
    }
  }

  //accessor methods
  def link(url: String): ZIO[Surfer, FailLinkError, Unit] =
    ZIO.accessM(_.get.link(url))

  val url: RIO[Surfer, String] =
    ZIO.accessM(_.get.url())
}
