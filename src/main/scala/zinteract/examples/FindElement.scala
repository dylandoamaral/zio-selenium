package zinteract.example

import zio.{App, ExitCode}
import zio.console

import zinteract.session
import zinteract.webdriver.ChromeBuilder

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- session.link("https://www.selenium.dev/documentation/en/")
    element <- session.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        ChromeBuilder(pathToDriver).buildLayer >>> session.Session.Service.live
      )
      .exitCode
}
