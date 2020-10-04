package zinteract

import zio.{IO, UIO, ZIO}

import org.openqa.selenium.{By, Keys, NoSuchElementException, SearchContext, WebElement}

import scala.jdk.CollectionConverters._

package object context {
  def findElementFrom(context: SearchContext)(by: By): IO[NoSuchElementException, WebElement] =
    ZIO.effect(context.findElement(by)).refineToOrDie[NoSuchElementException]

  def findElementByIdFrom(context: SearchContext)(id: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ById(id))

  def findElementByTagNameFrom(context: SearchContext)(tag: String): IO[NoSuchElementException, WebElement] =
    findElementByCssSelectorFrom(context)(tag)

  def findElementByClassFrom(context: SearchContext)(classname: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ByClassName(classname))

  def findElementByNameFrom(context: SearchContext)(name: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ByName(name))

  def findElementByXPathFrom(context: SearchContext)(xpath: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ByXPath(xpath))

  def findElementByCssSelectorFrom(context: SearchContext)(selector: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ByCssSelector(selector))

  def findElementByLinkTextFrom(context: SearchContext)(text: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ByLinkText(text))

  def findElementByPartialLinkTextFrom(context: SearchContext)(text: String): IO[NoSuchElementException, WebElement] =
    findElementFrom(context)(new By.ByPartialLinkText(text))

  def findElementsFrom(context: SearchContext)(by: By): UIO[List[WebElement]] =
    ZIO.effect(context.findElements(by).asScala.toList).orElseSucceed(List[WebElement]())

  def findElementsByIdFrom(context: SearchContext)(id: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ById(id))

  def findElementsByTagNameFrom(context: SearchContext)(tag: String): UIO[List[WebElement]] =
    findElementsByCssSelectorFrom(context)(tag)

  def findElementsByClassFrom(context: SearchContext)(classname: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ByClassName(classname))

  def findElementsByNameFrom(context: SearchContext)(name: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ByName(name))

  def findElementsByXPathFrom(context: SearchContext)(xpath: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ByXPath(xpath))

  def findElementsByCssSelectorFrom(context: SearchContext)(selector: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ByCssSelector(selector))

  def findElementsByLinkTextFrom(context: SearchContext)(text: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ByLinkText(text))

  def findElementsByPartialLinkTextFrom(context: SearchContext)(text: String): UIO[List[WebElement]] =
    findElementsFrom(context)(new By.ByPartialLinkText(text))

  def hasElementFrom(context: SearchContext)(by: By): UIO[Boolean] =
    findElementsFrom(context)(by).map(!_.isEmpty)

  def hasElementWithIdFrom(context: SearchContext)(id: String): UIO[Boolean] =
    findElementsByIdFrom(context)(id).map(!_.isEmpty)

  def hasElementWithTagNameFrom(context: SearchContext)(tag: String): UIO[Boolean] =
    findElementsByTagNameFrom(context)(tag).map(!_.isEmpty)

  def hasElementWithClassFrom(context: SearchContext)(classname: String): UIO[Boolean] =
    findElementsByClassFrom(context)(classname).map(!_.isEmpty)

  def hasElementWithNameFrom(context: SearchContext)(name: String): UIO[Boolean] =
    findElementsByNameFrom(context)(name).map(!_.isEmpty)

  def hasElementWithXPathFrom(context: SearchContext)(xpath: String): UIO[Boolean] =
    findElementsByXPathFrom(context)(xpath).map(!_.isEmpty)

  def hasElementWithCssSelectorFrom(context: SearchContext)(selector: String): UIO[Boolean] =
    findElementsByCssSelectorFrom(context)(selector).map(!_.isEmpty)

  def hasElementWithLinkTextFrom(context: SearchContext)(text: String): UIO[Boolean] =
    findElementsByLinkTextFrom(context)(text).map(!_.isEmpty)

  def hasElementWithPartialLinkTextFrom(context: SearchContext)(text: String): UIO[Boolean] =
    findElementsByPartialLinkTextFrom(context)(text).map(!_.isEmpty)
}
