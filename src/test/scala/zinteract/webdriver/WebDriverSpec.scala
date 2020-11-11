package zinteract.test

import zio.ZIO
import zio.duration.durationInt
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.test.TestDriver.testLayer
import zinteract.webdriver
import zinteract.element._

import org.openqa.selenium.{By, Cookie, NoSuchCookieException, TimeoutException, WebDriverException}
import org.openqa.selenium.support.ui.WebDriverWait

import scala.io.Source

object WebDriverSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/WebDriverSpec.html").getPath
  val testWebsite = s"file://$testPath"
  val fakeValue   = "9b233f60-01c2-11eb-adc1-0242ac120002"

  def suiteWebDriver =
    suite("WebDriver Spec")(
      testM("WebDriver can go back") {
        val effect = webdriver.link("https://www.google.com/") *>
          webdriver.link("https://duckduckgo.com/") *>
          webdriver.navigate.back *>
          webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("WebDriver can go forward") {
        val effect = webdriver.link("https://www.google.com/") *>
          webdriver.link("https://duckduckgo.com/") *>
          webdriver.navigate.back *>
          webdriver.navigate.forward *>
          webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://duckduckgo.com/"))
      },
      testM("WebDriver can refresh") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          input  <- webdriver.findElement(By.id("input"))
          _      <- input.sendKeysM("test")
          _      <- webdriver.navigate.refresh
          input2 <- webdriver.findElement(By.id("input"))
          text   <- input2.getTextM
        } yield assert(text)(isEmptyString)

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can get page source") {
        def clean(page: String): String =
          page.split("\n").drop(1).map(_.replace(" ", "")).mkString

        val file   = Source.fromFile(testPath)
        val source = file.getLines.mkString("\n")

        val effect = for {
          _    <- webdriver.link(testWebsite)
          raw  <- webdriver.source
          html <- ZIO.succeed(raw.replace("\r\n", "\n"))
        } yield assert(clean(html))(equalTo(clean(source)))

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteUrl =
    suite("Url Spec")(
      testM("WebDriver should link to about:blank by default") {
        val effect = webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("about:blank"))
      },
      testM("WebDriver should link correctly to a correct url using `navigate.to`") {
        val effect = webdriver.navigate.to("https://www.google.com/") *> webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("WebDriver should link correctly to a correct url using `link`") {
        val effect = webdriver.link("https://www.google.com/") *> webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("WebDriver should link correctly to a correct url using `goto`") {
        val effect = webdriver.goto("https://www.google.com/") *> webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("WebDriver should link correctly to a correct url using `get`") {
        val effect = webdriver.get("https://www.google.com/") *> webdriver.url

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("https://www.google.com/"))
      },
      testM("WebDriver shouldn't link to an incorrect url") {
        val effect = webdriver.link(fakeValue)

        assertM(effect.provideCustomLayer(testLayer()).run)(
          fails(isSubtype[WebDriverException](anything))
        )
      },
      testM("WebDriver should link correctly to a correct domain") {
        val effect = webdriver.link("https://github.com/") *> webdriver.domain

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("github.com"))
      },
      testM("WebDriver should link correctly to a correct domain with www") {
        val effect = webdriver.link("https://www.google.com/") *> webdriver.domain

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("google.com"))
      },
      testM("WebDriver should link to a page with a title") {
        val effect = webdriver.link("https://www.google.com/") *> webdriver.title

        assertM(effect.provideCustomLayer(testLayer()))(equalTo("Google"))
      }
    )

  def suiteElements =
    suite("Find Elements Spec")(
      testM("WebDriver can find an element") {
        val effect = for {
          result  <- webdriver.link(testWebsite)
          element <- webdriver.findElement(new By.ById("test"))
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can find several elements") {
        val effect = for {
          result  <- webdriver.link(testWebsite)
          element <- webdriver.findElements(By.id("test"))
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts)(equalTo(List("Test 1", "Test 2")))

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver return empty list if no elements") {
        val effect = for {
          result  <- webdriver.link(testWebsite)
          element <- webdriver.findElements(By.id("notest"))
        } yield assert(element)(isEmpty)

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can check if an element exist") {
        val effect = for {
          result <- webdriver.link(testWebsite)
          bool   <- webdriver.hasElement(By.id("test"))
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteCookies =
    suite("Cookies Spec")(
      testM("WebDriver can add a cookie") {
        val effect = for {
          _ <- webdriver.link("https://www.google.com/")
          _ <- webdriver.manage.addCookie(new Cookie("key", "value"))
        } yield assertCompletes

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can add a cookie using key and value") {
        val effect = for {
          _ <- webdriver.link("https://www.google.com/")
          _ <- webdriver.manage.addCookie("key", "value")
        } yield assertCompletes

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can get a cookie") {
        val effect = for {
          _      <- webdriver.link("https://www.google.com/")
          _      <- webdriver.manage.addCookie("key", "value")
          cookie <- webdriver.manage.getCookieNamed("key")
        } yield assert(cookie.getName)(equalTo("key")) && assert(cookie.getValue)(equalTo("value"))

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can get several cookies") {
        val effect = for {
          _        <- webdriver.link("https://www.google.com/")
          _        <- webdriver.manage.deleteAllCookies
          nCookies <- zio.random.nextIntBetween(1, 10)
          _        <- ZIO.foreach(1 to nCookies)(index => webdriver.manage.addCookie(s"key $index", s"value $index"))
          cookies  <- webdriver.manage.getAllCookies
        } yield assert(cookies.length)(equalTo(nCookies)) &&
          assert(cookies(0).getName.toSeq)(startsWith("key"))

        Live.live(effect.provideCustomLayer(testLayer()))
      },
      testM("WebDriver can delete a cookie") {
        val effect = for {
          _      <- webdriver.link("https://www.google.com/")
          before <- webdriver.manage.getAllCookies
          _      <- webdriver.manage.addCookie(new Cookie("key", "value", "google.com", null, null))
          cookie <- webdriver.manage.getCookieNamed("key")
          _      <- webdriver.manage.deleteCookie(cookie)
          after  <- webdriver.manage.getAllCookies
        } yield assert(before.length)(equalTo(after.length))

        effect.provideCustomLayer(testLayer())
      },
      testM("WebDriver can delete a cookie by name") {
        val effect = webdriver.link("https://www.google.com/") *>
          webdriver.manage.addCookie("key", "value") *>
          webdriver.manage.deleteCookieNamed("key") *>
          webdriver.manage.getCookieNamed("key")

        assertM(effect.provideCustomLayer(testLayer()).run)(
          fails(isSubtype[NoSuchCookieException](anything))
        )
      },
      testM("WebDriver can delete many cookies") {
        val effect = for {
          _         <- webdriver.link("https://www.google.com/")
          _         <- webdriver.manage.addCookie("key 0", "value 0")
          _         <- webdriver.manage.addCookie("key 1", "value 1")
          cookies   <- webdriver.manage.getAllCookies
          _         <- webdriver.manage.deleteAllCookies
          nocookies <- webdriver.manage.getAllCookies
        } yield assert(cookies.length)(isGreaterThan(0)) && assert(nocookies)(isEmpty)

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteAlerts =
    suite("Alerts Spec")(
      testM("WebDriver can handle an alert giving a polling and timeout duration") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.tagName("button"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield assert(alert.getText())(equalTo("Test alert"))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("WebDriver can handle an alert giving a wait") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.tagName("button"))
          _      <- button.clickM
          wait   <- webdriver.underlying.map(new WebDriverWait(_, 3))
          alert  <- webdriver.getAlert(wait)
        } yield assert(alert.getText())(equalTo("Test alert"))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("WebDriver return a timeout exception if no alert") {
        val effect = webdriver.link(testWebsite) *> webdriver.getAlert(100.milliseconds, 500.milliseconds)

        assertM(effect.provideCustomLayer(testLayer(false, true)).run)(
          fails(isSubtype[TimeoutException](anything))
        )
      }
    )

  def spec = suite("WebDriver Spec")(suiteWebDriver, suiteUrl, suiteElements, suiteCookies, suiteAlerts)
}
