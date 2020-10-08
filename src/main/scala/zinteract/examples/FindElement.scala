package zinteract.examples

import zio.{App, ExitCode, ZIO}
import zio.console

import zinteract.webdriver.WebDriver
import zinteract.surfer

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- surfer.link("https://www.selenium.dev/documentation/en/")
    element <- surfer.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> surfer.Surfer.Service.live
      )
      .exitCode
}
