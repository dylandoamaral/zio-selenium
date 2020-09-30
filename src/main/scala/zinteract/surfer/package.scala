package zinteract

import zio.{Task, IO, Has, UIO, RIO, ZIO, ZLayer}

import zinteract.webdriver.WebDriver

import org.openqa.selenium.{By, WebElement}

import scala.jdk.CollectionConverters._

package object surfer {
  type Surfer = Has[Surfer.Service]

  object Surfer extends Serializable {
    trait Service extends Serializable {
      def link(url: String): IO[FailLinkError, Unit]
      def url(): UIO[String]

      def findElement(by: By): UIO[Option[WebElement]]
      def findElementById(id: String): UIO[Option[WebElement]]
      def findElementByClass(classname: String): UIO[Option[WebElement]]
      def findElementByName(name: String): UIO[Option[WebElement]]
      def findElementByTagName(tag: String): UIO[Option[WebElement]]
      def findElementByXPath(xpath: String): UIO[Option[WebElement]]
      def findElementByCssSelector(selector: String): UIO[Option[WebElement]]
      def findElementByLinkText(link: String): UIO[Option[WebElement]]
      def findElementByPartialLinkText(text: String): UIO[Option[WebElement]]

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
    }

    object Service {
      val live: ZLayer[WebDriver, Nothing, Surfer] =
        ZLayer.fromService(webdriver =>
          new Surfer.Service {
            def link(url: String): IO[FailLinkError, Unit] =
              ZIO.effect(webdriver.get(url)).mapError(_ => FailLinkError(url))

            def url(): UIO[String] =
              ZIO.effect(webdriver.getCurrentUrl).orElse(ZIO.succeed("about:blank"))

            def findElement(by: By): UIO[Option[WebElement]] =
              ZIO.effect(Some(webdriver.findElement(by))).orElseSucceed(None)

            def findElementById(id: String): UIO[Option[WebElement]] = findElement(new By.ById(id))

            def findElementByTagName(tag: String): UIO[Option[WebElement]] = findElementByCssSelector(tag)

            def findElementByClass(classname: String): UIO[Option[WebElement]] =
              findElement(new By.ByClassName(classname))

            def findElementByName(name: String): UIO[Option[WebElement]] = findElement(new By.ByName(name))

            def findElementByXPath(xpath: String): UIO[Option[WebElement]] = findElement(new By.ByXPath(xpath))

            def findElementByCssSelector(selector: String): UIO[Option[WebElement]] =
              findElement(new By.ByCssSelector(selector))

            def findElementByLinkText(text: String): UIO[Option[WebElement]] =
              findElement(new By.ByLinkText(text))

            def findElementByPartialLinkText(text: String): UIO[Option[WebElement]] =
              findElement(new By.ByPartialLinkText(text))

            def findElements(by: By): UIO[List[WebElement]] =
              ZIO.effect(webdriver.findElements(by).asScala.toList).orElseSucceed(List[WebElement]())

            def findElementsById(id: String): UIO[List[WebElement]] = findElements(new By.ById(id))

            def findElementsByTagName(tag: String): UIO[List[WebElement]] = findElementsByCssSelector(tag)

            def findElementsByClass(classname: String): UIO[List[WebElement]] =
              findElements(new By.ByClassName(classname))

            def findElementsByName(name: String): UIO[List[WebElement]] = findElements(new By.ByName(name))

            def findElementsByXPath(xpath: String): UIO[List[WebElement]] = findElements(new By.ByXPath(xpath))

            def findElementsByCssSelector(selector: String): UIO[List[WebElement]] =
              findElements(new By.ByCssSelector(selector))

            def findElementsByLinkText(text: String): UIO[List[WebElement]] =
              findElements(new By.ByLinkText(text))

            def findElementsByPartialLinkText(text: String): UIO[List[WebElement]] =
              findElements(new By.ByPartialLinkText(text))

            def hasElement(by: By): UIO[Boolean] =
              findElements(by).map(!_.isEmpty)

            def hasElementWithId(id: String): UIO[Boolean] = findElementsById(id).map(!_.isEmpty)

            def hasElementWithTagName(tag: String): UIO[Boolean] = findElementsByTagName(tag).map(!_.isEmpty)

            def hasElementWithClass(classname: String): UIO[Boolean] =
              findElementsByClass(classname).map(!_.isEmpty)

            def hasElementWithName(name: String): UIO[Boolean] = findElementsByName(name).map(!_.isEmpty)

            def hasElementWithXPath(xpath: String): UIO[Boolean] = findElementsByXPath(xpath).map(!_.isEmpty)

            def hasElementWithCssSelector(selector: String): UIO[Boolean] =
              findElementsByCssSelector(selector).map(!_.isEmpty)

            def hasElementWithLinkText(text: String): UIO[Boolean] =
              findElementsByLinkText(text).map(!_.isEmpty)

            def hasElementWithPartialLinkText(text: String): UIO[Boolean] =
              findElementsByPartialLinkText(text).map(!_.isEmpty)
          }
        )
    }
  }

  //accessor methods
  def link(url: String): ZIO[Surfer, FailLinkError, Unit] =
    ZIO.accessM(_.get.link(url))

  val url: RIO[Surfer, String] =
    ZIO.accessM(_.get.url)

  def findElement(by: By): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElement(by))

  def findElementById(id: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementById(id))

  def findElementByClass(classname: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementByClass(classname))

  def findElementByName(name: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementByName(name))

  def findElementByTagName(tag: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementByTagName(tag))

  def findElementByXPath(xpath: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementByXPath(xpath))

  def findElementByCssSelector(selector: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementByCssSelector(selector))

  def findElementByLinkText(text: String): RIO[Surfer, Option[WebElement]] =
    ZIO.accessM(_.get.findElementByLinkText(text))

  def findElementByPartialLinkText(text: String): RIO[Surfer, Option[WebElement]] =
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

}
