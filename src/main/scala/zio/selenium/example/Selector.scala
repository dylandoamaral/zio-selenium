package zio.selenium.example

import zio._
import zio.selenium.Selector._
import zio.selenium._

import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver

object Selector extends ZIOAppDefault {

  val app: ZIO[WebDriver, Throwable, Unit] =
    for {
      _       <- WebDriver.get("https://github.com/dylandoamaral/zinteract")
      element <- WebDriver.findElement(by(href equalsTo "/dylandoamaral/zinteract" in a))
      _       <- Console.printLine(s"Project: ${element.getText}")
    } yield ()

  val layer: Layer[WebDriverException, WebDriver] = WebDriver.layer(new ChromeDriver())

  override def run = app.provide(layer)
}
