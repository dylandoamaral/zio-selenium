package zio.selenium.example

import org.openqa.selenium.WebDriverException
import org.openqa.selenium.chrome.ChromeDriver

import zio._
import zio.selenium._

object Link extends ZIOAppDefault {

  val app: ZIO[WebDriver, WebDriverException, Unit] =
    for {
      _ <- WebDriver.get("https://www.selenium.dev/documentation/en/")
    } yield ()

  val layer: Layer[WebDriverException, WebDriver] = WebDriver.layer(new ChromeDriver())

  override def run = app.provide(layer)
}
