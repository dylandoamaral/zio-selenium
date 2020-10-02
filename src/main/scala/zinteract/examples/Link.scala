package zinteract

import zio.{App, ExitCode, ZIO}

import zinteract.webdriver.WebDriver
import zinteract.surfer.Surfer

object Link extends App {
  val app = for {
    _ <- surfer.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> Surfer.Service.live
      )
      .exitCode
}
