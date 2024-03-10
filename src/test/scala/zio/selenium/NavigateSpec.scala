package zio.selenium

import org.openqa.selenium.{By, WebDriverException}

import zio.test._
import zio.test.TestAspect.sequential

object NavigateSpec extends SharedWebDriverSpec {

  val testPath: String = getClass.getResource("/WebDriverSpec.html").getPath
  val testWebsite      = s"file://$testPath"
  val trueWebsite      = "https://github.com/"
  val fakeValue        = "9b233f60-01c2-11eb-adc1-0242ac120002"

  override def spec: Spec[WebDriver, WebDriverException] =
    suite("Navigate Spec")(
//      TODO: it dies with java.net.MalformedURLException and not fails, maybe we should have our own error handling
//      TODO: system or wait scala 3 intersection type. I don't know why the test doesn't work.
//      test("WebDriver shouldn't link to an incorrect url") {
//        val assertion = dies(isSubtype[MalformedURLException](anything))
//        val effect    = WebDriver.get(fakeValue).exit.map(v => assert(v)(assertion))
//
//        effect.provide(webdriverEmptyLayer)
//      },
      test("Navigate can go back") {
        for {
          _   <- WebDriver.get(trueWebsite)
          _   <- WebDriver.get(testWebsite)
          _   <- WebDriver.Navigate.back
          url <- WebDriver.getCurrentUrl
        } yield assertTrue(url == trueWebsite)
      },
      test("Navigate can go forward") {
        for {
          _   <- WebDriver.get(trueWebsite)
          _   <- WebDriver.Navigate.back
          _   <- WebDriver.Navigate.forward
          url <- WebDriver.getCurrentUrl
        } yield assertTrue(url == trueWebsite)
      },
      test("Navigate can refresh") {
        for {
          _      <- WebDriver.get(testWebsite)
          input  <- WebDriver.findElement(By.id("input"))
          _      <- input.sendKeys("test")
          _      <- WebDriver.Navigate.refresh
          input2 <- WebDriver.findElement(By.id("input"))
          text   <- input2.getText
        } yield assertTrue(text.isEmpty)
      }
    ) @@ sequential
}
