package zinteract

import zio._

import org.openqa.selenium.{By, SearchContext, WebElement}

import scala.jdk.CollectionConverters._

/** Context provides methods to find an element which are use by webdriver and element packages.
  */
package object context {

  /** Finds all WebElements using the given method.
    */
  def findElementFrom(
      context: SearchContext
  )(by: By)(implicit wait: WaitKind = DontWait): ZIO[Clock, Throwable, WebElement] = {
    val effect = wait match {
      case DontWait                  => ZIO.attemptBlocking(context.findElement(by))
      case WaitUsingSelenium(waiter) => ZIO.attemptBlocking(waiter.until(_ => context.findElement(by)))
      case WaitUsingZIO(schedule)    => ZIO.attemptBlocking(context.findElement(by)).retry(schedule)
    }
    effect
  }

  /** Finds all WebElements using the given method.
    */
  def findElementsFrom(
      context: SearchContext
  )(by: By)(implicit wait: WaitKind = DontWait): RIO[Clock, List[WebElement]] = {
    val effect = wait match {
      case DontWait => ZIO.attemptBlocking(context.findElements(by).asScala)
      case _        => findElementFrom(context)(by)(wait).fold(_ => List(), _ => context.findElements(by).asScala)
    }

    effect.map(_.toList)
  }

  /** Checks if the given method find an element.
    */
  def hasElementFrom(context: SearchContext)(by: By)(implicit wait: WaitKind = DontWait): RIO[Clock, Boolean] =
    findElementsFrom(context)(by)(wait).map(_.nonEmpty)
}
