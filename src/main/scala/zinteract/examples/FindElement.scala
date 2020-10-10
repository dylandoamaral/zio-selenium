package zinteract.example

import zio.{App, ExitCode, ZIO}
import zio.console

import zinteract.webdriver.WebDriver
import zinteract.session

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
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> session.Session.Service.live
      )
      .exitCode
}
