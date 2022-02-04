package zinteract.context

import zio._

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait

/** An interface to define different ways to wait for an element. You has two ways to do that, by using selenium fluent
  * wait or by using ZIO schedule.
  *
  * e.g: The page has a loading screen.
  */
sealed trait WaitKind
case object DontWait                                               extends WaitKind
case class WaitUsingSelenium(waiter: FluentWait[WebDriver])        extends WaitKind
case class WaitUsingZIO(schedule: Schedule[Clock, Throwable, Any]) extends WaitKind
