package zinteract

import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zio.console._
import zinteract.surfer
import zinteract.webdriver
import zinteract.environment.TestSurfer
import zio.ZIO

object SurferSpec extends DefaultRunnableSpec {
  def spec =
    suite("SurferSpec")(
      testM("surfer link correctly to a website") {
        val effect = for {
          _   <- TestSurfer.feedUrls("www.google.com")
          _   <- surfer.link("www.google.com")
          url <- TestSurfer.url
        } yield assert(url)(equalTo("www.google.com"))

        effect.provideCustomLayer(TestSurfer.live)
      },
      testM("surfer don't link an incorrect url") {
        val effect = for {
          result <- surfer.link("www.google.com").run
        } yield assert(result)(fails(equalTo(FailLinkError("www.google.com"))))

        effect.provideCustomLayer(TestSurfer.live)
      }
    )
}
