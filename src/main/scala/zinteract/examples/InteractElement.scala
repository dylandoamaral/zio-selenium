package zinteract.examples

import org.openqa.selenium.chrome.ChromeOptions
import zio._
import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.{RemoteBuilder, chrome}
import zinteract.element._
import zinteract.webdriver
import org.openqa.selenium.{By, WebDriver}

object InteractElement extends ZIOAppDefault {
  val app: ZIO[WebDriver with Clock, Throwable, Unit] = for {
    _          <- webdriver.link("https://www.selenium.dev/documentation/en/")
    search     <- webdriver.findElement(By.cssSelector("[type=search]"))
    _          <- search.sendKeysM("Introduction")
    _          <- Clock.sleep(2.seconds)
    suggestion <- webdriver.findElement(By.className("autocomplete-suggestion"))
    _          <- suggestion.clickM
    _          <- Clock.sleep(2.seconds)
  } yield ()

  val builder: RemoteBuilder[ChromeOptions] = chrome at "/path/to/chromedriver" using default

  override def run: URIO[ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
