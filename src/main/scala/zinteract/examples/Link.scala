package zinteract.example

import zio.{App, ExitCode}

import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.chrome
import zinteract.webdriver

object Link extends App {
  val app = for {
    _ <- webdriver.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
