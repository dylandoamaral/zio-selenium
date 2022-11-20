package zio.selenium

import zio._
import zio.test._

abstract class SharedWebDriverSpec extends ZIOSpec[WebDriver] {
  override val bootstrap: ULayer[WebDriver] = webdriverLayer(cssEnabled = false).orDie
}
