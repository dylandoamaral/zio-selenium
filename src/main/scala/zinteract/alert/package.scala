package zinteract

import zio.{IO, RIO, Task, ZIO}
import zio.clock.Clock

import zinteract.context._

import org.openqa.selenium.{Alert}

package object alert {

  implicit class ZinteractAlert(alert: Alert) {
    val acceptM: Task[Unit] =
      acceptOn(alert)

    val dismissM: Task[Unit] =
      dismissOn(alert)

    def sendKeysM(text: String): Task[Unit] =
      sendKeysOn(alert)(text)

    val getTextM: Task[String] =
      getTextOn(alert)
  }

  def acceptOn(alert: Alert): Task[Unit] =
    ZIO.effect(alert.accept)

  def dismissOn(alert: Alert): Task[Unit] =
    ZIO.effect(alert.dismiss)

  def sendKeysOn(alert: Alert)(text: String): Task[Unit] =
    ZIO.effect(alert.sendKeys(text))

  def getTextOn(alert: Alert): Task[String] =
    ZIO.effect(alert.getText)
}
