package zinteract.example

import zio.{App, ExitCode, console}

import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.chrome
import zinteract.webdriver

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- webdriver.link("https://www.selenium.dev/documentation/en/")
    element <- webdriver.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
