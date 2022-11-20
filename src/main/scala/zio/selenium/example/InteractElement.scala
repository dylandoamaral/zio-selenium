package zio.selenium.example

import org.openqa.selenium.{By, WebDriverException}
import org.openqa.selenium.chrome.ChromeDriver

import zio._
import zio.selenium._

object InteractElement extends ZIOAppDefault {

  val app: ZIO[WebDriver with Clock, Throwable, Unit] =
    for {
      _          <- WebDriver.get("https://www.selenium.dev/documentation/en/")
      search     <- WebDriver.findElement(By.cssSelector("[type=search]"))
      _          <- search.sendKeys("Introduction")
      _          <- Clock.sleep(2.seconds)
      suggestion <- WebDriver.findElement(By.className("autocomplete-suggestion"))
      _          <- suggestion.click
      _          <- Clock.sleep(2.seconds)
    } yield ()

  val layer: Layer[WebDriverException, WebDriver] = WebDriver.layer(new ChromeDriver())

  override def run = app.provide(layer)
}
