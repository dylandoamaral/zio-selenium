package zinteract.example

import zio.{App, ExitCode}
import zio.console

import zinteract.session
import zinteract.webdriver.ChromeBlueprintOps.default
import zinteract.webdriver.BuilderOps.chrome

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- session.link("https://www.selenium.dev/documentation/en/")
    element <- session.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
