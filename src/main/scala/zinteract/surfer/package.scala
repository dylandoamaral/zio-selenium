package zinteract

import zio.{Task, IO, Has, ZIO, ZLayer}
import zinteract.webdriver.WebDriver

package object surfer {
  type Surfer = Has[Surfer.Service]

  object Surfer extends Serializable {
    trait Service extends Serializable {
      def link(url: String): IO[FailLinkError, Unit]
    }

    object Service {
      val live: ZLayer[WebDriver, Nothing, Surfer] =
        ZLayer.fromService(webdriver =>
          new Surfer.Service {
            def link(url: String): IO[FailLinkError, Unit] =
              ZIO.effect(webdriver.get(url)).orDieWith(t => FailLinkError(url))
          }
        )
    }
  }

  //accessor methods
  def link(url: String): ZIO[Surfer, FailLinkError, Unit] =
    ZIO.accessM(_.get.link(url))
}
