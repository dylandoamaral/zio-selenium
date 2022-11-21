package zio.selenium.example

import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver

import zio._
import zio.selenium._
import zio.selenium.Selector._

object Selector extends ZIOAppDefault {

  val app: ZIO[WebDriver, Throwable, Unit] =
    for {
      _       <- WebDriver.get("https://github.com/dylandoamaral/zio-selenium")
      element <- WebDriver.findElement(by(href equalsTo "/dylandoamaral/zio-selenium" in a))
      _       <- Console.printLine(s"Project: ${element.getText}")
    } yield ()

  val layer: Layer[WebDriverException, WebDriver] = WebDriver.layer(new ChromeDriver())

  override def run = app.provide(layer)
}
