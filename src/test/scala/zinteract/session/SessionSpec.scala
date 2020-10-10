package zinteract.test

import zio.ZIO
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.test.TestDriver.testLayer
import zinteract.session

import org.openqa.selenium.By

object SessionSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/SessionSpec.html").getPath
  val testWebsite = s"file://$testPath"
  val fakeValue   = "9b233f60-01c2-11eb-adc1-0242ac120002"

  def suiteWebDriver =
    suite("WebDriver Spec")(
      testM("Session navigate using a webdriver") {
        val effect = for {
          webdriver <- session.getWebdriver
        } yield assert(webdriver.getCurrentUrl())(equalTo("about:blank"))

        effect.provideCustomLayer(testLayer())
      }
    )
  def suiteUrl =
    suite("Url Spec")(
      testM("Session should link to about:blank by default") {
        val effect = for {
          url <- session.url
        } yield assert(url)(equalTo("about:blank"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Session should link correctly to a correct url") {
        val effect = for {
          _   <- session.link("https://www.google.com/")
          url <- session.url
        } yield assert(url)(equalTo("https://www.google.com/"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Session shouldn't link to an incorrect url") {
        val effect = for {
          result <- session.link(fakeValue)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer()).run)(
          fails(isSubtype[org.openqa.selenium.WebDriverException](anything))
        )
      },
      testM("Session should link correctly to a correct domain") {
        val effect = for {
          _   <- session.link("https://github.com/")
          url <- session.domain
        } yield assert(url)(equalTo("github.com"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Session should link correctly to a correct domain with www") {
        val effect = for {
          _   <- session.link("https://www.google.com/")
          url <- session.domain
        } yield assert(url)(equalTo("google.com"))

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteElements =
    suite("Find Elements Spec")(
      testM("Session can find an element") {
        val effect = for {
          result  <- session.link(testWebsite)
          element <- session.findElement(new By.ById("test"))
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Session can find several elements") {
        val effect = for {
          result  <- session.link(testWebsite)
          element <- session.findElements(By.id("test"))
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts)(equalTo(List("Test 1", "Test 2")))

        effect.provideCustomLayer(testLayer())
      },
      testM("Session return empty list if no elements") {
        val effect = for {
          result  <- session.link(testWebsite)
          element <- session.findElements(By.id("notest"))
        } yield assert(element)(isEmpty)

        effect.provideCustomLayer(testLayer())
      },
      testM("Session can check if an element exist") {
        val effect = for {
          result <- session.link(testWebsite)
          bool   <- session.hasElement(By.id("test"))
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer())
      }
    )

  def spec = suite("Session Spec")(suiteUrl, suiteElements)
}
