package zinteract.examples

import zio._

import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.{RemoteBuilder, chrome}
import zinteract.context.Selector.{a, by, href}
import zinteract.webdriver

import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeOptions

object Selector extends ZIOAppDefault {
  val app: ZIO[Console with WebDriver with Clock, Throwable, Unit] = for {
    _       <- webdriver.link("https://github.com/dylandoamaral/zinteract")
    element <- webdriver.findElement(by(href equalsTo "/dylandoamaral/zinteract" in a))
    _       <- Console.printLine(s"Project: ${element.getText}")
  } yield ()

  val builder: RemoteBuilder[ChromeOptions] = chrome at "/path/to/chromedriver" using default

  override def run: URIO[ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
