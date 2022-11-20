package zio.selenium

import org.openqa.selenium.{By, WebDriverException}

import zio._
import zio.test._
import zio.test.TestAspect.sequential

import scala.io.Source

object WebDriverSpec extends SharedWebDriverSpec {

  val testPath: String = getClass.getResource("/WebDriverSpec.html").getPath
  val testWebsite      = s"file://$testPath"
  val trueWebsite      = "https://github.com/"

  override def spec: Spec[WebDriver, WebDriverException] =
    suite("WebDriver Spec")(
      test("WebDriver should get the current URL") {
        for {
          _   <- WebDriver.get(trueWebsite)
          url <- WebDriver.getCurrentUrl
        } yield assertTrue(url == trueWebsite)
      },
      test("WebDriver should get the correct domain") {
        for {
          _      <- WebDriver.get("https://github.com/")
          domain <- WebDriver.getDomain
        } yield assertTrue(domain == "github.com")
      },
      test("WebDriver should get the correct domain (with www)") {
        for {
          _      <- WebDriver.get("https://www.github.com/")
          domain <- WebDriver.getDomain
        } yield assertTrue(domain == "github.com")
      },
      test("WebDriver should get the correct page title") {
        for {
          _     <- WebDriver.get(testWebsite)
          title <- WebDriver.getTitle
        } yield assertTrue(title == "SessionSpec")
      },
      test("WebDriver can get page source") {
        def clean(page: String): String = page.split("\n").drop(1).map(_.replace(" ", "")).mkString

        val file   = Source.fromFile(testPath)
        val source = file.getLines.mkString("\n")

        for {
          _    <- WebDriver.get(testWebsite)
          raw  <- WebDriver.getPageSource
          html <- ZIO.succeed(raw.replace("\r\n", "\n"))
        } yield assertTrue(clean(html) == clean(source))
      },
      test("WebDriver can find an element") {
        for {
          _       <- WebDriver.get(testWebsite)
          element <- WebDriver.findElement(new By.ById("test"))
          text    <- element.getText
        } yield assertTrue(text == "Test 1")
      },
      test("WebDriver can find several elements") {
        for {
          _        <- WebDriver.get(testWebsite)
          elements <- WebDriver.findElements(By.id("test"))
          texts    <- ZIO.foreach(elements)(_.getText)
        } yield assertTrue(texts == List("Test 1", "Test 2"))
      },
      test("WebDriver return empty list if no elements") {
        for {
          _        <- WebDriver.get(testWebsite)
          elements <- WebDriver.findElements(By.id("notest"))
        } yield assertTrue(elements.isEmpty)
      },
      test("WebDriver can check if an element exist") {
        for {
          _    <- WebDriver.get(testWebsite)
          bool <- WebDriver.hasElement(By.id("test"))
        } yield assertTrue(bool)
      }
    ) @@ sequential
}
