package zinteract

import zio.Schedule
import zio.clock.Clock

import org.openqa.selenium.WebDriver
import org.openqa.selenium.support.ui.FluentWait

sealed trait WaitConfig
case object None                                                extends WaitConfig
case class Fluent(waiter: FluentWait[WebDriver])                extends WaitConfig
case class Scheduled(schedule: Schedule[Clock, Throwable, Any]) extends WaitConfig
