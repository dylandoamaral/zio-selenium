package zinteract

import zio.clock.Clock
import zio.{RIO, ZIO}

import org.openqa.selenium.{By, SearchContext, WebElement}

import java.util.ArrayList
import scala.jdk.CollectionConverters._

/** Context provides methods to find an element which are use by webdriver and element packages.
  */
package object context {

  /** Finds all WebElements using the given method.
    */
  def findElementFrom(
      context: SearchContext
  )(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, Throwable, WebElement] = {
    val effect = wait match {
      case None                => ZIO.effect(context.findElement(by))
      case Fluent(waiter)      => ZIO.effect(waiter.until(_ => context.findElement(by)))
      case Scheduled(schedule) => ZIO.effect(context.findElement(by)).retry(schedule)
    }
    effect
  }

  /** Finds all WebElements using the given method.
    */
  def findElementsFrom(
      context: SearchContext
  )(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]] = {
    val effect = wait match {
      case None => ZIO.effect(context.findElements(by))
      case Fluent(waiter) =>
        findElementFrom(context)(by)(wait).fold(_ => new ArrayList(), _ => context.findElements(by))
      case Scheduled(schedule) =>
        findElementFrom(context)(by)(wait).fold(_ => new ArrayList(), _ => context.findElements(by))
    }
    effect.map(_.asScala.toList)
  }

  /** Checks if the given method find an element.
    */
  def hasElementFrom(context: SearchContext)(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean] =
    findElementsFrom(context)(by)(wait).map(!_.isEmpty)
}
