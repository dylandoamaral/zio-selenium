package zinteract.examples

import zio._

import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.{RemoteBuilder, chrome}
import zinteract.webdriver

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.{By, WebDriver}

object FindElement extends ZIOAppDefault {
  val app: ZIO[Console with WebDriver with Clock, Throwable, Unit] = for {
    _       <- webdriver.link("https://www.selenium.dev/documentation/en/")
    element <- webdriver.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- Console.printLine(s"Title: ${element.getText}")
  } yield ()

  val builder: RemoteBuilder[ChromeOptions] = chrome at "/path/to/chromedriver" using default

  override def run: URIO[ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
