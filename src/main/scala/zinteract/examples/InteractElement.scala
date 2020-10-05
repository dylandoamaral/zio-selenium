package zinteract.examples

import zio.{App, ExitCode, ZIO}
import zio.console
import zio.clock
import zio.duration.durationInt

import zinteract.webdriver.WebDriver
import zinteract.surfer
import zinteract.element._

object InteractElement extends App {
  val app = for {
    _          <- surfer.link("https://www.selenium.dev/documentation/en/")
    search     <- surfer.findElementByCssSelector("[type=search]")
    _          <- search.sendKeysM("Introduction")
    _          <- clock.sleep(2.seconds)
    suggestion <- surfer.findElementByClass("autocomplete-suggestion")
    _          <- suggestion.clickM
    _          <- clock.sleep(2.seconds)
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> surfer.Surfer.Service.live
      )
      .exitCode
}
