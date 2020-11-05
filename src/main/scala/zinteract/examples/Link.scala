package zinteract.example

import zio.{App, ExitCode}

import zinteract.session
import zinteract.webdriver.ChromeBlueprintOps.default
import zinteract.webdriver.BuilderOps.chrome

object Link extends App {
  val app = for {
    _ <- session.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val builder = chrome at "/path/to/webdriver/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
