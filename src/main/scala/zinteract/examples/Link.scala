package zinteract.example

import zio.{App, ExitCode}

import zinteract.session
import zinteract.webdriver.ChromeBuilder

object Link extends App {
  val app = for {
    _ <- session.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(ChromeBuilder(pathToDriver).buildLayer)
      .exitCode
}
