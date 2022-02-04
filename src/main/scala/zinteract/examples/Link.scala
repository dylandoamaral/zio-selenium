package zinteract.examples

import zio._

import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.{RemoteBuilder, chrome}
import zinteract.webdriver

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.{WebDriver, WebDriverException}

object Link extends ZIOAppDefault {
  val app: ZIO[WebDriver, WebDriverException, Unit] = for {
    _ <- webdriver.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val builder: RemoteBuilder[ChromeOptions] = chrome at "/path/to/chromedriver" using default

  override def run: URIO[ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
