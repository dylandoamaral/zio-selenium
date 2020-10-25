package zinteract

import zio.{RIO, Task, ZIO}
import zio.clock.Clock

import zinteract.context._

import org.openqa.selenium.{By, Dimension, Keys, NoSuchElementException, Point, Rectangle, WebElement}

package object element {

  implicit class ZinteractWebElement(element: WebElement) {
    val clickM: Task[Unit] =
      clickOn(element)

    val clearM: Task[Unit] =
      clearOn(element)

    def sendKeysM(text: CharSequence): Task[Unit] =
      sendKeysOn(element)(text)

    val pressEnterM: Task[Unit] =
      pressEnterOn(element)

    val submitM: Task[Unit] =
      submitOn(element)

    def getAttributeM(name: String): Task[String] =
      getAttributeOf(element)(name)

    val getTextM: Task[String] =
      getTextOf(element)

    def getCssValueM(propertyName: String): Task[String] =
      getCssValueOf(element)(propertyName)

    val getTagNameM: Task[String] =
      getTagNameOf(element)

    val getLocationM: Task[Point] =
      getLocationOf(element)

    val getRectM: Task[Rectangle] =
      getRectOf(element)

    val getSizeM: Task[Dimension] =
      getSizeOf(element)

    val isDisplayedM: Task[Boolean] =
      isDisplayed(element)

    val isEnabledM: Task[Boolean] =
      isEnabled(element)

    val isSelectedM: Task[Boolean] =
      isSelected(element)

    def findElementM(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, NoSuchElementException, WebElement] =
      findElementFrom(element)(by)(wait)

    def findElementsM(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]] =
      findElementsFrom(element)(by)(wait)

    def hasElementM(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean] =
      hasElementFrom(element)(by)(wait)
  }

  def clickOn(element: WebElement): Task[Unit] =
    ZIO.effect(element.click)

  def clearOn(element: WebElement): Task[Unit] =
    ZIO.effect(element.clear)

  def sendKeysOn(element: WebElement)(text: CharSequence): Task[Unit] =
    ZIO.effect(element.sendKeys(text))

  def pressEnterOn(element: WebElement): Task[Unit] =
    sendKeysOn(element)(Keys.ENTER)

  def submitOn(element: WebElement): Task[Unit] =
    ZIO.effect(element.submit)

  def getAttributeOf(element: WebElement)(name: String): Task[String] =
    ZIO.effect(element.getAttribute(name))

  def getTextOf(element: WebElement): Task[String] =
    ZIO.effect(element.getText)

  def getCssValueOf(element: WebElement)(propertyName: String): Task[String] =
    ZIO.effect(element.getCssValue(propertyName))

  def getTagNameOf(element: WebElement): Task[String] =
    ZIO.effect(element.getTagName)

  def getLocationOf(element: WebElement): Task[Point] =
    ZIO.effect(element.getLocation)

  def getRectOf(element: WebElement): Task[Rectangle] =
    ZIO.effect(element.getRect)

  def getSizeOf(element: WebElement): Task[Dimension] =
    ZIO.effect(element.getSize)

  def isDisplayed(element: WebElement): Task[Boolean] =
    ZIO.effect(element.isDisplayed)

  def isEnabled(element: WebElement): Task[Boolean] =
    ZIO.effect(element.isEnabled)

  def isSelected(element: WebElement): Task[Boolean] =
    ZIO.effect(element.isSelected)
}
