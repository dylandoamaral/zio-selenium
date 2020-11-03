package zinteract.context

import zio.Schedule
import zio.clock.Clock

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait

/**
  * An interface to define different ways to wait for an element.
  * You has two ways to do that, by using selenium fluent wait or
  * by using ZIO schedule.
  *
  * e.g: The page has a loading screen.
  */
sealed trait WaitConfig
case object None                                                extends WaitConfig
case class Fluent(waiter: FluentWait[WebDriver])                extends WaitConfig
case class Scheduled(schedule: Schedule[Clock, Throwable, Any]) extends WaitConfig
