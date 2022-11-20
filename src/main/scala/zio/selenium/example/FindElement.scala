package zio.selenium.example

import zio._
import zio.selenium._

import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.{By, WebDriverException}

object FindElement extends ZIOAppDefault {

  val app: ZIO[WebDriver, Throwable, Unit] =
    for {
      _       <- WebDriver.get("https://www.selenium.dev/documentation/en/")
      element <- WebDriver.findElement(By.id("the-selenium-browser-automation-project"))
      text    <- element.getText
      _       <- Console.printLine(s"Title: $text")
    } yield ()

  val layer: Layer[WebDriverException, WebDriver] = WebDriver.layer(new ChromeDriver())

  override def run = app.provide(layer)
}
