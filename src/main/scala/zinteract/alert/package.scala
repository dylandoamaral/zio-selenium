package zinteract

import zio.{Task, ZIO}

import org.openqa.selenium.{Alert}

/** Alert provides a way to interact purely with alert.
  */
package object alert {

  implicit class ZinteractAlert(alert: Alert) {

    /** Accepts an alert.
      */
    val acceptM: Task[Unit] =
      acceptOn(alert)

    /** Dismisses an alert.
      */
    val dismissM: Task[Unit] =
      dismissOn(alert)

    /** Simulates typing into an alert.
      */
    def sendKeysM(text: String): Task[Unit] =
      sendKeysOn(alert)(text)

    /** Gets the text of the alert.
      */
    val getTextM: Task[String] =
      getTextOn(alert)
  }

  /** Accepts an alert.
    */
  def acceptOn(alert: Alert): Task[Unit] =
    ZIO.effect(alert.accept)

  /** Dismisses an alert.
    */
  def dismissOn(alert: Alert): Task[Unit] =
    ZIO.effect(alert.dismiss)

  /** Simulates typing into an alert.
    */
  def sendKeysOn(alert: Alert)(text: String): Task[Unit] =
    ZIO.effect(alert.sendKeys(text))

  /** Gets the text of the alert.
    */
  def getTextOn(alert: Alert): Task[String] =
    ZIO.effect(alert.getText)
}
