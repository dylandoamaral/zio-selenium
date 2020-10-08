package zinteract

import zio.{Task, IO, Has, UIO, RIO, ZIO, ZLayer}
import zio.clock.Clock
import zio.duration.{Duration, durationInt}

import zinteract.webdriver.WebDriver
import zinteract.context._

import org.openqa.selenium.{By, NoSuchElementException, WebElement, WebDriver => SeleniumWebDriver}
import org.openqa.selenium.support.ui.{FluentWait, Wait}
import java.net.URI

import scala.jdk.CollectionConverters._

package object surfer {
  type Surfer = Has[Surfer.Service]

  object Surfer extends Serializable {
    trait Service extends Serializable {
      def link(url: String): Task[Unit]
      def url(): UIO[String]
      def domain(): UIO[String]

      def findElement(by: By)(implicit wait: WaitConfig): ZIO[Clock, NoSuchElementException, WebElement]
      def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]]
      def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean]

      val getWebdriver: UIO[SeleniumWebDriver]
      def getFluentWaiter(polling: Duration, timeout: Duration): UIO[Fluent]
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

            def findElement(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, NoSuchElementException, WebElement] =
              findElementFrom(webdriver)(by)(wait)

            def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]] =
              findElementsFrom(webdriver)(by)(wait)

            def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean] =
              hasElementFrom(webdriver)(by)(wait)

            val getWebdriver: UIO[SeleniumWebDriver] = ZIO.succeed(webdriver)

            def getFluentWaiter(polling: Duration, timeout: Duration): UIO[Fluent] =
              ZIO.succeed(
                Fluent(
                  new FluentWait[SeleniumWebDriver](webdriver)
                    .pollingEvery(polling.asJava)
                    .withTimeout(timeout.asJava)
                    .ignoring(classOf[NoSuchElementException])
                )
              )
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

  def findElement(
      by: By
  )(implicit wait: WaitConfig = None): ZIO[Surfer with Clock, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElement(by)(wait))

  def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Surfer with Clock, List[WebElement]] =
    ZIO.accessM(_.get.findElements(by)(wait))

  def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Surfer with Clock, Boolean] =
    ZIO.accessM(_.get.hasElement(by)(wait))

  val getWebdriver: RIO[Surfer, SeleniumWebDriver] =
    ZIO.accessM(_.get.getWebdriver)

  def getFluentWaiter(polling: Duration, timeout: Duration): RIO[Surfer, Fluent] =
    ZIO.accessM(_.get.getFluentWaiter(timeout, polling))
}
