package zinteract

import zio.{Task, IO, Has, UIO, RIO, ZIO, ZLayer}

import zinteract.webdriver.WebDriver
import zinteract.context._

import org.openqa.selenium.{By, NoSuchElementException, WebElement, WebDriver => SeleniumWebDriver}
import java.net.URI

import scala.jdk.CollectionConverters._

package object surfer {
  type Surfer = Has[Surfer.Service]

  object Surfer extends Serializable {
    trait Service extends Serializable {
      def link(url: String): Task[Unit]
      def url(): UIO[String]
      def domain(): UIO[String]

      def findElement(by: By): IO[NoSuchElementException, WebElement]
      def findElementById(id: String): IO[NoSuchElementException, WebElement]
      def findElementByClass(classname: String): IO[NoSuchElementException, WebElement]
      def findElementByName(name: String): IO[NoSuchElementException, WebElement]
      def findElementByTagName(tag: String): IO[NoSuchElementException, WebElement]
      def findElementByXPath(xpath: String): IO[NoSuchElementException, WebElement]
      def findElementByCssSelector(selector: String): IO[NoSuchElementException, WebElement]
      def findElementByLinkText(link: String): IO[NoSuchElementException, WebElement]
      def findElementByPartialLinkText(text: String): IO[NoSuchElementException, WebElement]

      def findElements(by: By): UIO[List[WebElement]]
      def findElementsById(id: String): UIO[List[WebElement]]
      def findElementsByClass(classname: String): UIO[List[WebElement]]
      def findElementsByName(name: String): UIO[List[WebElement]]
      def findElementsByTagName(tag: String): UIO[List[WebElement]]
      def findElementsByXPath(xpath: String): UIO[List[WebElement]]
      def findElementsByCssSelector(selector: String): UIO[List[WebElement]]
      def findElementsByLinkText(link: String): UIO[List[WebElement]]
      def findElementsByPartialLinkText(text: String): UIO[List[WebElement]]

      def hasElement(by: By): UIO[Boolean]
      def hasElementWithId(id: String): UIO[Boolean]
      def hasElementWithClass(classname: String): UIO[Boolean]
      def hasElementWithName(name: String): UIO[Boolean]
      def hasElementWithTagName(tag: String): UIO[Boolean]
      def hasElementWithXPath(xpath: String): UIO[Boolean]
      def hasElementWithCssSelector(selector: String): UIO[Boolean]
      def hasElementWithLinkText(link: String): UIO[Boolean]
      def hasElementWithPartialLinkText(text: String): UIO[Boolean]

      val getWebdriver: UIO[SeleniumWebDriver]
    }

    object Service {
      val live: ZLayer[WebDriver, Nothing, Surfer] =
        ZLayer.fromService(webdriver =>
          new Surfer.Service {
            def link(url: String): Task[Unit] =
              ZIO.effect(webdriver.get(url))

            def url(): UIO[String] =
              ZIO.effect(webdriver.getCurrentUrl).orElse(ZIO.succeed("about:blank"))

            def domain(): UIO[String] =
              url.map(url => {
                val uri: URI       = new URI(url)
                val domain: String = uri.getHost();

                if (domain.startsWith("www.")) domain.substring(4) else domain
              })

            def findElement(by: By): IO[NoSuchElementException, WebElement] =
              findElementFrom(webdriver)(by)

            def findElementById(id: String): IO[NoSuchElementException, WebElement] = findElementByIdFrom(webdriver)(id)

            def findElementByTagName(tag: String): IO[NoSuchElementException, WebElement] =
              findElementByTagNameFrom(webdriver)(tag)

            def findElementByClass(classname: String): IO[NoSuchElementException, WebElement] =
              findElementByClassFrom(webdriver)(classname)

            def findElementByName(name: String): IO[NoSuchElementException, WebElement] =
              findElementByNameFrom(webdriver)(name)

            def findElementByXPath(xpath: String): IO[NoSuchElementException, WebElement] =
              findElementByXPathFrom(webdriver)(xpath)

            def findElementByCssSelector(selector: String): IO[NoSuchElementException, WebElement] =
              findElementByCssSelectorFrom(webdriver)(selector)

            def findElementByLinkText(text: String): IO[NoSuchElementException, WebElement] =
              findElementByLinkTextFrom(webdriver)(text)

            def findElementByPartialLinkText(text: String): IO[NoSuchElementException, WebElement] =
              findElementByPartialLinkTextFrom(webdriver)(text)

            def findElements(by: By): UIO[List[WebElement]] =
              findElementsFrom(webdriver)(by)

            def findElementsById(id: String): UIO[List[WebElement]] = findElementsByIdFrom(webdriver)(id)

            def findElementsByTagName(tag: String): UIO[List[WebElement]] = findElementsByTagNameFrom(webdriver)(tag)

            def findElementsByClass(classname: String): UIO[List[WebElement]] =
              findElementsByClassFrom(webdriver)(classname)

            def findElementsByName(name: String): UIO[List[WebElement]] =
              findElementsByNameFrom(webdriver)(name)

            def findElementsByXPath(xpath: String): UIO[List[WebElement]] =
              findElementsByXPathFrom(webdriver)(xpath)

            def findElementsByCssSelector(selector: String): UIO[List[WebElement]] =
              findElementsByCssSelectorFrom(webdriver)(selector)

            def findElementsByLinkText(text: String): UIO[List[WebElement]] =
              findElementsByLinkTextFrom(webdriver)(text)

            def findElementsByPartialLinkText(text: String): UIO[List[WebElement]] =
              findElementsByPartialLinkTextFrom(webdriver)(text)

            def hasElement(by: By): UIO[Boolean] =
              hasElementFrom(webdriver)(by)

            def hasElementWithId(id: String): UIO[Boolean] = hasElementWithIdFrom(webdriver)(id)

            def hasElementWithTagName(tag: String): UIO[Boolean] = hasElementWithTagNameFrom(webdriver)(tag)

            def hasElementWithClass(classname: String): UIO[Boolean] =
              hasElementWithClassFrom(webdriver)(classname)

            def hasElementWithName(name: String): UIO[Boolean] = hasElementWithNameFrom(webdriver)(name)

            def hasElementWithXPath(xpath: String): UIO[Boolean] = hasElementWithXPathFrom(webdriver)(xpath)

            def hasElementWithCssSelector(selector: String): UIO[Boolean] =
              hasElementWithCssSelectorFrom(webdriver)(selector)

            def hasElementWithLinkText(text: String): UIO[Boolean] =
              hasElementWithLinkTextFrom(webdriver)(text)

            def hasElementWithPartialLinkText(text: String): UIO[Boolean] =
              hasElementWithPartialLinkTextFrom(webdriver)(text)

            val getWebdriver: UIO[SeleniumWebDriver] = ZIO.succeed(webdriver)
          }
        )
    }
  }

  //accessor methods
  def link(url: String): ZIO[Surfer, Throwable, Unit] =
    ZIO.accessM(_.get.link(url))

  val url: RIO[Surfer, String] =
    ZIO.accessM(_.get.url)

  val domain: RIO[Surfer, String] =
    ZIO.accessM(_.get.domain)

  def findElement(by: By): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElement(by))

  def findElementById(id: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementById(id))

  def findElementByClass(classname: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByClass(classname))

  def findElementByName(name: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByName(name))

  def findElementByTagName(tag: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByTagName(tag))

  def findElementByXPath(xpath: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByXPath(xpath))

  def findElementByCssSelector(selector: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByCssSelector(selector))

  def findElementByLinkText(text: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByLinkText(text))

  def findElementByPartialLinkText(text: String): ZIO[Surfer, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElementByPartialLinkText(text))

  def findElements(by: By): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElements(by))

  def findElementsById(id: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsById(id))

  def findElementsByClass(classname: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByClass(classname))

  def findElementsByName(name: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByName(name))

  def findElementsByTagName(tag: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByTagName(tag))

  def findElementsByXPath(xpath: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByXPath(xpath))

  def findElementsByCssSelector(selector: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByCssSelector(selector))

  def findElementsByLinkText(text: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByLinkText(text))

  def findElementsByPartialLinkText(text: String): RIO[Surfer, List[WebElement]] =
    ZIO.accessM(_.get.findElementsByPartialLinkText(text))

  def hasElement(by: By): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElement(by))

  def hasElementWithId(id: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithId(id))

  def hasElementWithClass(classname: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithClass(classname))

  def hasElementWithName(name: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithName(name))

  def hasElementWithTagName(tag: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithTagName(tag))

  def hasElementWithXPath(xpath: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithXPath(xpath))

  def hasElementWithCssSelector(selector: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithCssSelector(selector))

  def hasElementWithLinkText(text: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithLinkText(text))

  def hasElementWithPartialLinkText(text: String): RIO[Surfer, Boolean] =
    ZIO.accessM(_.get.hasElementWithPartialLinkText(text))

  val getWebdriver: RIO[Surfer, SeleniumWebDriver] =
    ZIO.accessM(_.get.getWebdriver)
}
