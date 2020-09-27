package zinteract

import zinteract.surfer.Surfer
import zinteract.webdriver.WebDriver
import zio.{FiberRef, Has, Layer, IO, Task, UIO, Ref, ZEnv, ZLayer, ZManaged, ZIO}

package object environment {
  type TestSurfer = Has[TestSurfer.Service]

  object TestSurfer {
    case class Data(urls: Seq[String], currentUrl: String)

    trait Service {
      def feedUrls(lines: String*): UIO[Unit]
      def url: UIO[String]
    }

    case class Test(
        surferState: Ref[TestSurfer.Data]
    ) extends Surfer.Service
        with TestSurfer.Service {

      def feedUrls(lines: String*): UIO[Unit] = surferState.update(data => data.copy(urls = lines.toSeq ++ data.urls))

      def url: UIO[String] = surferState.get.map(_.currentUrl)

      def link(url: String): IO[FailLinkError, Unit] =
        surferState.get.flatMap(data =>
          data.urls.contains(url) match {
            case true  => surferState.update(data => data.copy(currentUrl = url))
            case false => IO.fail(FailLinkError(url))
          }
        )
    }

    def feedUrls(lines: String*): ZIO[TestSurfer, Unit, Unit] =
      ZIO.accessM(_.get.feedUrls(lines: _*))

    val url: ZIO[TestSurfer, Unit, String] =
      ZIO.accessM(_.get.url)

    def make(data: Data): ZLayer[Any, Nothing, Surfer with TestSurfer] =
      ZLayer.fromEffectMany(
        for {
          ref <- Ref.make(data)
          test = Test(ref)
        } yield Has.allOf[Surfer.Service, TestSurfer.Service](test, test)
      )

    def live: ZLayer[Any, Nothing, Surfer with TestSurfer] =
      make(Data(List.empty, ""))
  }
}
