package zio.selenium

import zio._
import zio.test._

abstract class SharedWebDriverWithCssSpec extends ZIOSpec[WebDriver] {
  override val bootstrap: ULayer[WebDriver] = webdriverLayer(cssEnabled = true).orDie
}
