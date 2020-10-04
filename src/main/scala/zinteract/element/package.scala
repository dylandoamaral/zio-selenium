package zinteract

import zio.{IO, UIO, Task, ZIO}

import zinteract.context._

import org.openqa.selenium.{By, Dimension, Keys, Point, NoSuchElementException, Rectangle, WebElement}

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

    def findElementM(by: By): IO[NoSuchElementException, WebElement] =
      findElementFrom(element)(by)

    def findElementByIdM(id: String): IO[NoSuchElementException, WebElement] = findElementByIdFrom(element)(id)

    def findElementByTagNameM(tag: String): IO[NoSuchElementException, WebElement] =
      findElementByTagNameFrom(element)(tag)

    def findElementByClassM(classname: String): IO[NoSuchElementException, WebElement] =
      findElementByClassFrom(element)(classname)

    def findElementByNameM(name: String): IO[NoSuchElementException, WebElement] =
      findElementByNameFrom(element)(name)

    def findElementByXPathM(xpath: String): IO[NoSuchElementException, WebElement] =
      findElementByXPathFrom(element)(xpath)

    def findElementByCssSelectorM(selector: String): IO[NoSuchElementException, WebElement] =
      findElementByCssSelectorFrom(element)(selector)

    def findElementByLinkTextM(text: String): IO[NoSuchElementException, WebElement] =
      findElementByLinkTextFrom(element)(text)

    def findElementByPartialLinkTextM(text: String): IO[NoSuchElementException, WebElement] =
      findElementByPartialLinkTextFrom(element)(text)

    def findElementsM(by: By): UIO[List[WebElement]] =
      findElementsFrom(element)(by)

    def findElementsByIdM(id: String): UIO[List[WebElement]] = findElementsByIdFrom(element)(id)

    def findElementsByTagNameM(tag: String): UIO[List[WebElement]] = findElementsByTagNameFrom(element)(tag)

    def findElementsByClassM(classname: String): UIO[List[WebElement]] =
      findElementsByClassFrom(element)(classname)

    def findElementsByNameM(name: String): UIO[List[WebElement]] =
      findElementsByNameFrom(element)(name)

    def findElementsByXPathM(xpath: String): UIO[List[WebElement]] =
      findElementsByXPathFrom(element)(xpath)

    def findElementsByCssSelectorM(selector: String): UIO[List[WebElement]] =
      findElementsByCssSelectorFrom(element)(selector)

    def findElementsByLinkTextM(text: String): UIO[List[WebElement]] =
      findElementsByLinkTextFrom(element)(text)

    def findElementsByPartialLinkTextM(text: String): UIO[List[WebElement]] =
      findElementsByPartialLinkTextFrom(element)(text)

    def hasElementM(by: By): UIO[Boolean] =
      hasElementFrom(element)(by)

    def hasElementWithIdM(id: String): UIO[Boolean] = hasElementWithIdFrom(element)(id)

    def hasElementWithTagNameM(tag: String): UIO[Boolean] = hasElementWithTagNameFrom(element)(tag)

    def hasElementWithClassM(classname: String): UIO[Boolean] =
      hasElementWithClassFrom(element)(classname)

    def hasElementWithNameM(name: String): UIO[Boolean] = hasElementWithNameFrom(element)(name)

    def hasElementWithXPathM(xpath: String): UIO[Boolean] = hasElementWithXPathFrom(element)(xpath)

    def hasElementWithCssSelectorM(selector: String): UIO[Boolean] =
      hasElementWithCssSelectorFrom(element)(selector)

    def hasElementWithLinkTextM(text: String): UIO[Boolean] =
      hasElementWithLinkTextFrom(element)(text)

    def hasElementWithPartialLinkTextM(text: String): UIO[Boolean] =
      hasElementWithPartialLinkTextFrom(element)(text)
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
