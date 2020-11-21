package zinteract.example

import zio.{console, App, ExitCode}

import zinteract.builder.ChromeBlueprint.default
import zinteract.builder.chrome
import zinteract.context.Selector.{a, by, href}
import zinteract.webdriver

object Selector extends App {
  val app = for {
    _       <- webdriver.link("https://github.com/dylandoamaral/zinteract")
    element <- webdriver.findElement(by(href equalsTo "/dylandoamaral/zinteract" in a))
    _       <- console.putStrLn(s"Project: ${element.getText()}")
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}
