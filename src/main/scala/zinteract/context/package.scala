package zinteract

import zio.{RIO, ZIO}
import zio.clock.Clock

import org.openqa.selenium.{By, Keys, NoSuchElementException, SearchContext, WebDriver, WebElement}
import org.openqa.selenium.support.ui.FluentWait

import scala.jdk.CollectionConverters._
import java.util.ArrayList
import java.time.Duration

package object context {
  def findElementFrom(
      context: SearchContext
  )(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, NoSuchElementException, WebElement] = {
    val effect = wait match {
      case None                => ZIO.effect(context.findElement(by))
      case Fluent(waiter)      => ZIO.effect(waiter.until(_ => context.findElement(by)))
      case Scheduled(schedule) => ZIO.effect(context.findElement(by)).retry(schedule)
    }
    effect.refineToOrDie[NoSuchElementException]
  }

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
    effect.map(_.asScala.toList).orElseSucceed(List[WebElement]())
  }

  def hasElementFrom(context: SearchContext)(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean] =
    findElementsFrom(context)(by)(wait).map(!_.isEmpty)
}
