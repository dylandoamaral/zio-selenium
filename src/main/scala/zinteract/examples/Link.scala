package zinteract.example

import zio.{App, ExitCode}

import zinteract.webdriver.WebDriver
import zinteract.session

object Link extends App {
  val app = for {
    _ <- session.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> session.Session.Service.live
      )
      .exitCode
}
