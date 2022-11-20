package zio.selenium

import org.openqa.selenium.{Cookie, WebDriverException}

import zio._
import zio.test._
import zio.test.TestAspect.sequential

object ManageSpec extends SharedWebDriverSpec {

  val trueWebsite = "https://github.com/"
  val fakeValue   = "9b233f60-01c2-11eb-adc1-0242ac120002"

  override def spec: Spec[WebDriver, WebDriverException] =
    suite("Manage Spec")(
      test("Manage can add a cookie") {
        for {
          _ <- WebDriver.get(trueWebsite)
          _ <- WebDriver.Manage.addCookie(new Cookie("key", "value"))
        } yield assertCompletes
      },
      test("WebDriver can add a cookie using key and value") {
        for {
          _ <- WebDriver.get(trueWebsite)
          _ <- WebDriver.Manage.addCookie("key", "value")
        } yield assertCompletes
      },
      test("WebDriver should return none for an unknown cookie") {
        for {
          _           <- WebDriver.get(trueWebsite)
          _           <- WebDriver.Manage.deleteCookieNamed("key")
          maybeCookie <- WebDriver.Manage.getCookieNamed("key")
        } yield assertTrue(maybeCookie.isEmpty)
      },
      test("WebDriver can get a cookie") {
        for {
          _           <- WebDriver.get(trueWebsite)
          _           <- WebDriver.Manage.addCookie("key", "value")
          maybeCookie <- WebDriver.Manage.getCookieNamed("key")
          cookie = maybeCookie.get
        } yield assertTrue(cookie.getName == "key") && assertTrue(cookie.getValue == "value")
      },
      test("WebDriver can delete a cookie") {
        for {
          _           <- WebDriver.get(trueWebsite)
          _           <- WebDriver.Manage.addCookie("key", "value")
          cookie      <- WebDriver.Manage.getCookieNamed("key").map(_.get)
          _           <- WebDriver.Manage.deleteCookie(cookie)
          maybeCookie <- WebDriver.Manage.getCookieNamed("key")
        } yield assertTrue(maybeCookie.isEmpty)
      },
      test("WebDriver can delete a cookie by name") {
        for {
          _           <- WebDriver.get(trueWebsite)
          _           <- WebDriver.Manage.addCookie("key", "value")
          _           <- WebDriver.Manage.deleteCookieNamed("key")
          maybeCookie <- WebDriver.Manage.getCookieNamed("key")
        } yield assertTrue(maybeCookie.isEmpty)
      },
      test("WebDriver can manage many cookies") {
        for {
          _        <- WebDriver.get(trueWebsite)
          _        <- WebDriver.Manage.deleteAllCookies
          nCookies <- Random.nextIntBetween(1, 10)
          newCookies = (1 to nCookies).map(index => new Cookie(s"key $index", s"value $index"))
          _       <- WebDriver.Manage.addCookies(newCookies)
          cookies <- WebDriver.Manage.getAllCookies
        } yield assertTrue(cookies.size == nCookies) && assertTrue(cookies.head.getName.toSeq.startsWith("key"))
      },
      test("WebDriver can delete many cookies") {
        for {
          _         <- WebDriver.get(trueWebsite)
          _         <- WebDriver.Manage.addCookie("key 0", "value 0")
          _         <- WebDriver.Manage.addCookie("key 1", "value 1")
          cookies   <- WebDriver.Manage.getAllCookies
          _         <- WebDriver.Manage.deleteAllCookies
          noCookies <- WebDriver.Manage.getAllCookies
        } yield assertTrue(cookies.nonEmpty) && assertTrue(noCookies.isEmpty)
      }
    ) @@ sequential
}
