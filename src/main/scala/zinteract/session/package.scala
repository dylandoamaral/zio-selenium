package zinteract

import zio.{Has, IO, RIO, Task, UIO, ZIO, ZLayer}
import zio.clock.Clock
import zio.duration.{durationInt, Duration}

import zinteract.webdriver.WebDriver
import zinteract.context._

import org.openqa.selenium.{By, NoSuchElementException, WebElement, WebDriver => SeleniumWebDriver}
import org.openqa.selenium.support.ui.{FluentWait, Wait}
import java.net.URI

import scala.jdk.CollectionConverters._

package object session {
  type Session = Has[Session.Service]

  object Session extends Serializable {
    trait Service extends Serializable {
      def back: UIO[Unit]
      def forward: UIO[Unit]
      def refresh: UIO[Unit]

      def link(url: String): Task[Unit]
      def url: UIO[String]
      def domain: UIO[String]
      def title: UIO[String]

      def findElement(by: By)(implicit wait: WaitConfig): ZIO[Clock, NoSuchElementException, WebElement]
      def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]]
      def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean]

      def getWebdriver: UIO[SeleniumWebDriver]
      def getFluentWaiter(polling: Duration, timeout: Duration): UIO[Fluent]
      def getPageSource: UIO[String]
    }

    object Service {
      val live: ZLayer[WebDriver, Nothing, Session] =
        ZLayer.fromService(webdriver =>
          new Session.Service {
            def back: UIO[Unit] =
              ZIO.succeed(webdriver.navigate().back)

            def forward: UIO[Unit] =
              ZIO.succeed(webdriver.navigate().forward)

            def refresh: UIO[Unit] =
              ZIO.succeed(webdriver.navigate().refresh)

            def link(url: String): Task[Unit] =
              ZIO.effect(webdriver.get(url))

            def url: UIO[String] =
              ZIO.effect(webdriver.getCurrentUrl).orElse(ZIO.succeed("about:blank"))

            def domain: UIO[String] =
              url.map(url => {
                val uri: URI       = new URI(url)
                val domain: String = uri.getHost();

                if (domain.startsWith("www.")) domain.substring(4) else domain
              })

            def title: UIO[String] =
              ZIO.effect(webdriver.getTitle).orElse(ZIO.succeed("title"))

            def findElement(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, NoSuchElementException, WebElement] =
              findElementFrom(webdriver)(by)(wait)

            def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]] =
              findElementsFrom(webdriver)(by)(wait)

            def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean] =
              hasElementFrom(webdriver)(by)(wait)

            def getWebdriver: UIO[SeleniumWebDriver] = ZIO.succeed(webdriver)

            def getFluentWaiter(polling: Duration, timeout: Duration): UIO[Fluent] =
              ZIO.succeed(
                Fluent(
                  new FluentWait[SeleniumWebDriver](webdriver)
                    .pollingEvery(polling.asJava)
                    .withTimeout(timeout.asJava)
                    .ignoring(classOf[NoSuchElementException])
                )
              )

            def getPageSource: UIO[String] = ZIO.succeed(webdriver.getPageSource)
          }
        )
    }
  }

  //accessor methods
  def link(url: String): ZIO[Session, Throwable, Unit] =
    ZIO.accessM(_.get.link(url))

  def back: RIO[Session, Unit] =
    ZIO.accessM(_.get.back)

  def forward: RIO[Session, Unit] =
    ZIO.accessM(_.get.forward)

  def refresh: RIO[Session, Unit] =
    ZIO.accessM(_.get.refresh)

  def url: RIO[Session, String] =
    ZIO.accessM(_.get.url)

  def domain: RIO[Session, String] =
    ZIO.accessM(_.get.domain)

  def title: RIO[Session, String] =
    ZIO.accessM(_.get.title)

  def findElement(
      by: By
  )(implicit wait: WaitConfig = None): ZIO[Session with Clock, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElement(by)(wait))

  def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Session with Clock, List[WebElement]] =
    ZIO.accessM(_.get.findElements(by)(wait))

  def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Session with Clock, Boolean] =
    ZIO.accessM(_.get.hasElement(by)(wait))

  def getWebdriver: RIO[Session, SeleniumWebDriver] =
    ZIO.accessM(_.get.getWebdriver)

  def getFluentWaiter(polling: Duration, timeout: Duration): RIO[Session, Fluent] =
    ZIO.accessM(_.get.getFluentWaiter(timeout, polling))

  def getPageSource: RIO[Session, String] =
    ZIO.accessM(_.get.getPageSource)
}
