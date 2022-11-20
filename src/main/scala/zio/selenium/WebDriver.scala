package zio.selenium

import org.openqa.selenium.{By, Cookie, WebDriver => SeleniumWebDriver, WebDriverException}

import zio._
import zio.selenium.internal._

import scala.jdk.CollectionConverters._

import java.net.{URI, URL}

trait WebDriver {
  def getCurrentUrl: IO[WebDriverException, String]
  def getPageSource: IO[WebDriverException, String]
  def getTitle: IO[WebDriverException, String]

  def getDomain: IO[WebDriverException, String] =
    getCurrentUrl.map { url =>
      val uri: URI       = new URI(url)
      val domain: String = uri.getHost

      if (domain.startsWith("www.")) domain.substring(4) else domain
    }

  def findElement(by: By): IO[WebDriverException, WebElement]
  def findElements(by: By): IO[WebDriverException, List[WebElement]]
  def hasElement(by: By): IO[WebDriverException, Boolean] = findElements(by).map(_.nonEmpty)

  def close: IO[WebDriverException, Unit]

  def underlying: SeleniumWebDriver

  def navigate: Navigate
  def manage: Manage
}

object WebDriver {

  /** Accessor functions */

  private def access[A](f: WebDriver => IO[WebDriverException, A]): ZIO[WebDriver, WebDriverException, A] =
    ZIO.serviceWithZIO[WebDriver](f)

  def get(url: URL): ZIO[WebDriver, WebDriverException, Unit]    = Navigate.to(url)
  def get(url: String): ZIO[WebDriver, WebDriverException, Unit] = Navigate.to(url)

  def getCurrentUrl: ZIO[WebDriver, WebDriverException, String] = access(_.getCurrentUrl)
  def getPageSource: ZIO[WebDriver, WebDriverException, String] = access(_.getPageSource)
  def getDomain: ZIO[WebDriver, WebDriverException, String]     = access(_.getDomain)
  def getTitle: ZIO[WebDriver, WebDriverException, String]      = access(_.getTitle)

  def findElement(by: By): ZIO[WebDriver, WebDriverException, WebElement] = access(_.findElement(by))

  def findElements(by: By): ZIO[WebDriver, WebDriverException, List[WebElement]] = access(_.findElements(by))

  def hasElement(by: By): ZIO[WebDriver, WebDriverException, Boolean] = access(_.hasElement(by))

  def close: ZIO[WebDriver, WebDriverException, Unit] = access(_.close)

  def underlying: ZIO[WebDriver, Nothing, SeleniumWebDriver] = ZIO.service[WebDriver].map(_.underlying)

  object Navigate {
    def to(url: URL): ZIO[WebDriver, WebDriverException, Unit]       = access(_.navigate.to(url))
    def to(string: String): ZIO[WebDriver, WebDriverException, Unit] = to(new URL(string))

    def back: ZIO[WebDriver, WebDriverException, Unit]    = access(_.navigate.back)
    def forward: ZIO[WebDriver, WebDriverException, Unit] = access(_.navigate.forward)
    def refresh: ZIO[WebDriver, WebDriverException, Unit] = access(_.navigate.refresh)
  }

  object Manage {
    def addCookie(cookie: Cookie): ZIO[WebDriver, WebDriverException, Unit] = access(_.manage.addCookie(cookie))

    def addCookie(key: String, value: String): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.addCookie(key, value))

    def addCookies(cookies: Seq[Cookie]): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.addCookies(cookies))

    def getCookieNamed(key: String): ZIO[WebDriver, WebDriverException, Option[Cookie]] =
      access(_.manage.getCookieNamed(key))

    def getAllCookies: ZIO[WebDriver, WebDriverException, Set[Cookie]] = access(_.manage.getAllCookies)

    def deleteCookie(cookie: Cookie): ZIO[WebDriver, WebDriverException, Unit] = access(_.manage.deleteCookie(cookie))

    def deleteCookieNamed(key: String): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.deleteCookieNamed(key))

    def deleteAllCookies: ZIO[WebDriver, WebDriverException, Unit] = access(_.manage.deleteAllCookies)
  }

  /** Live implementation */

  final case class WebDriverLive(driver: SeleniumWebDriver) extends WebDriver {

    override def getCurrentUrl: IO[WebDriverException, String] = attempt(driver.getCurrentUrl)
    override def getPageSource: IO[WebDriverException, String] = attempt(driver.getPageSource)
    override def getTitle: IO[WebDriverException, String]      = attempt(driver.getTitle)

    override def findElement(by: By): IO[WebDriverException, WebElement] = attemptWebElement(driver.findElement(by))

    override def findElements(by: By): IO[WebDriverException, List[WebElement]] =
      attemptWebElements(driver.findElements(by))

    override def close: IO[WebDriverException, Unit] = attempt(driver.close())

    override def navigate: Navigate =
      new Navigate {
        override def to(url: URL): IO[WebDriverException, Unit] = attempt(driver.navigate().to(url))
        override def back: IO[WebDriverException, Unit]         = attempt(driver.navigate().back())
        override def forward: IO[WebDriverException, Unit]      = attempt(driver.navigate().forward())
        override def refresh: IO[WebDriverException, Unit]      = attempt(driver.navigate().refresh())
      }

    override def manage: Manage =
      new Manage {

        override def addCookie(cookie: Cookie): IO[WebDriverException, Unit] =
          attempt(driver.manage().addCookie(cookie))

        override def getCookieNamed(key: String): IO[WebDriverException, Option[Cookie]] =
          attempt(driver.manage.getCookieNamed(key)).map(Option.apply)

        override def getAllCookies: IO[WebDriverException, Set[Cookie]] =
          attempt(driver.manage.getCookies.asScala.toList.toSet)

        override def deleteCookie(cookie: Cookie): IO[WebDriverException, Unit] =
          attempt(driver.manage.deleteCookie(cookie))

        override def deleteCookieNamed(key: String): IO[WebDriverException, Unit] =
          attempt(driver.manage.deleteCookieNamed(key))

        override def deleteAllCookies: IO[WebDriverException, Unit] = attempt(driver.manage.deleteAllCookies())
      }

    override def underlying: SeleniumWebDriver = driver
  }

  def layer(webdriver: SeleniumWebDriver): Layer[WebDriverException, WebDriver] = {
    val acquire = ZIO.attempt(webdriver).refineToOrDie[WebDriverException].map(WebDriverLive)
    ZLayer.scoped(ZIO.acquireRelease(acquire)(_.close.orDie))
  }

}
