package zinteract

import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zio.console._

import zinteract.webdriver.WebDriver
import zinteract.surfer.Surfer

object SurferSpec extends DefaultRunnableSpec {
  val testLayer = WebDriver.Service.htmlunitMinConfig >>> Surfer.Service.live

  def spec =
    suite("SurferSpec")(
      testM("surfer link correctly to a website") {
        val effect = for {
          _   <- surfer.link("https://www.selenium.dev/documentation/en/")
          url <- surfer.url
        } yield assert(url)(equalTo("https://www.selenium.dev/documentation/en/"))

        effect.provideCustomLayer(testLayer)
      },
      testM("surfer link to about:blank by default") {
        val effect = for {
          url <- surfer.url
        } yield assert(url)(equalTo("about:blank"))

        effect.provideCustomLayer(testLayer)
      },
      testM("surfer don't link an incorrect url") {
        val effect = for {
          result <- surfer.link("www.9b233f60-01c2-11eb-adc1-0242ac120002.com").run
        } yield assert(result)(fails(equalTo(FailLinkError("www.9b233f60-01c2-11eb-adc1-0242ac120002.com"))))

        effect.provideCustomLayer(testLayer)
      }
    )
}
