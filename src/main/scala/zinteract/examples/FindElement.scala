package zinteract

import zio.{App, ExitCode, ZIO}
import zio.console

import zinteract.webdriver.{Property, WebDriver}
import zinteract.surfer.Surfer

object FindElement extends App {
  val app = for {
    _       <- surfer.link("https://www.selenium.dev/documentation/en/")
    element <- surfer.findElementById("the-selenium-browser-automation-project")
    _ <- element match {
      case Some(value) => console.putStrLn(s"Title: ${value.getText()}")
      case None        => console.putStrLn("Title not found !")
    }
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> Surfer.Service.live
      )
      .exitCode
}
