package zio.selenium

import org.openqa.selenium.{WebDriverException, WebElement => SeleniumWebElement}

import zio._
import zio.selenium.WebElement.WebElementLive

import scala.jdk.CollectionConverters._

package object internal {

  def attempt[A](effect: => A): IO[WebDriverException, A] = ZIO.attempt(effect).refineToOrDie[WebDriverException]

  def attemptWebElement(
      effect: => SeleniumWebElement
  ): IO[WebDriverException, WebElement] = attempt(effect).map(WebElementLive)

  def attemptWebElements(
      effect: => java.util.List[SeleniumWebElement]
  ): IO[WebDriverException, List[WebElement]] = attempt(effect).map(_.asScala.toList.map(WebElementLive))

}
