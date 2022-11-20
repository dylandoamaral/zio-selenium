package zio.selenium.example

import org.openqa.selenium.{By, WebDriverException}
import org.openqa.selenium.chrome.ChromeDriver

import zio._
import zio.selenium._

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
