package zinteract.example

import zio.{App, ExitCode}
import zio.clock
import zio.duration.durationInt

import zinteract.session
import zinteract.element._
import zinteract.webdriver.ChromeBlueprintOps.default
import zinteract.webdriver.BuilderOps.chrome

import org.openqa.selenium.By

object InteractElement extends App {
  val app = for {
    _          <- session.link("https://www.selenium.dev/documentation/en/")
    search     <- session.findElement(By.cssSelector("[type=search]"))
    _          <- search.sendKeysM("Introduction")
    _          <- clock.sleep(2.seconds)
    suggestion <- session.findElement(By.className("autocomplete-suggestion"))
    _          <- suggestion.clickM
    _          <- clock.sleep(2.seconds)
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
