package zinteract

import zio.ZIO
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.TestDriver.testLayer
import zinteract.surfer.Surfer

import org.openqa.selenium.By

object SurferSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/SurferSpec.html").getPath
  val testWebsite = s"file://$testPath"
  val fakeValue   = "9b233f60-01c2-11eb-adc1-0242ac120002"

  def suiteWebDriver =
    suite("WebDriver Spec")(
      testM("Surfer navigate using a webdriver") {
        val effect = for {
          webdriver <- surfer.getWebdriver
        } yield assert(webdriver.getCurrentUrl())(equalTo("about:blank"))

        effect.provideCustomLayer(testLayer())
      }
    )
  def suiteUrl =
    suite("Url Spec")(
      testM("Surfer should link to about:blank by default") {
        val effect = for {
          url <- surfer.url
        } yield assert(url)(equalTo("about:blank"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Surfer should link correctly to a correct url") {
        val effect = for {
          _   <- surfer.link("https://www.google.com/")
          url <- surfer.url
        } yield assert(url)(equalTo("https://www.google.com/"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Surfer shouldn't link to an incorrect url") {
        val effect = for {
          result <- surfer.link(fakeValue)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer()).run)(
          fails(isSubtype[org.openqa.selenium.WebDriverException](anything))
        )
      },
      testM("Surfer should link correctly to a correct domain") {
        val effect = for {
          _   <- surfer.link("https://github.com/")
          url <- surfer.domain
        } yield assert(url)(equalTo("github.com"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Surfer should link correctly to a correct domain with www") {
        val effect = for {
          _   <- surfer.link("https://www.google.com/")
          url <- surfer.domain
        } yield assert(url)(equalTo("google.com"))

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteElements =
    suite("Find Elements Spec")(
      testM("Surfer can find an element") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElement(new By.ById("test"))
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Surfer can find several elements") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElements(By.id("test"))
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts)(equalTo(List("Test 1", "Test 2")))

        effect.provideCustomLayer(testLayer())
      },
      testM("Surfer return empty list if no elements") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElements(By.id("notest"))
        } yield assert(element)(isEmpty)

        effect.provideCustomLayer(testLayer())
      },
      testM("Surfer can check if an element exist") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElement(By.id("test"))
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer())
      }
    )

  def spec = suite("Surfer Spec")(suiteUrl, suiteElements)
}
