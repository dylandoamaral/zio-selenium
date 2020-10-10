package zinteract.test

import zio.ZIO
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.test.TestDriver.testLayer
import zinteract.session
import zinteract.element._

import org.openqa.selenium.By

import scala.io.Source

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
      },
      testM("Session can go back") {
        val effect = session.link("https://www.google.com/") *>
          session.link("https://duckduckgo.com/") *>
          session.back *>
          session.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("Session can go forward") {
        val effect = session.link("https://www.google.com/") *>
          session.link("https://duckduckgo.com/") *>
          session.back *>
          session.forward *>
          session.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://duckduckgo.com/"))
      },
      testM("Session can refresh") {
        val effect = for {
          _      <- session.link(testWebsite)
          input  <- session.findElement(By.id("input"))
          _      <- input.sendKeysM("test")
          _      <- session.refresh
          input2 <- session.findElement(By.id("input"))
          text   <- input2.getTextM
        } yield assert(text)(isEmptyString)

        effect.provideCustomLayer(testLayer())
      },
      testM("Session can get page source") {
        def clean(page: String): String =
          page.split("\n").drop(1).map(_.replace(" ", "")).mkString

        val file   = Source.fromFile(testPath)
        val source = file.getLines.mkString("\n")

        val effect = for {
          _    <- session.link(testWebsite)
          raw  <- session.getPageSource
          html <- ZIO.succeed(raw.replace("\r\n", "\n"))
        } yield assert(clean(html))(equalTo(clean(source)))

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteUrl =
    suite("Url Spec")(
      testM("Session should link to about:blank by default") {
        val effect = session.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("about:blank"))
      },
      testM("Session should link correctly to a correct url") {
        val effect = session.link("https://www.google.com/") *> session.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("Session shouldn't link to an incorrect url") {
        val effect = session.link(fakeValue)

        assertM(effect.provideCustomLayer(testLayer()).run)(
          fails(isSubtype[org.openqa.selenium.WebDriverException](anything))
        )
      },
      testM("Session should link correctly to a correct domain") {
        val effect = session.link("https://github.com/") *> session.domain

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("github.com"))
      },
      testM("Session should link correctly to a correct domain with www") {
        val effect = session.link("https://www.google.com/") *> session.domain

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("google.com"))
      },
      testM("Session should link to a page with a title") {
        val effect = session.link("https://www.google.com/") *> session.title

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("Google"))
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

  def spec = suite("Session Spec")(suiteWebDriver, suiteUrl, suiteElements)
}
