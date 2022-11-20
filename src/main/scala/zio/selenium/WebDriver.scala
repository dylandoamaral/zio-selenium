package zio.selenium

import org.openqa.selenium.{By, Cookie, WebDriver => SeleniumWebDriver, WebDriverException}

import zio._
import zio.selenium.internal._

import scala.jdk.CollectionConverters._

import java.net.{URI, URL}

trait WebDriver {
  def getCurrentUrl(implicit trace: Trace): IO[WebDriverException, String]
  def getPageSource(implicit trace: Trace): IO[WebDriverException, String]
  def getTitle(implicit trace: Trace): IO[WebDriverException, String]

  def getDomain(implicit trace: Trace): IO[WebDriverException, String] =
    getCurrentUrl.map { url =>
      val uri: URI       = new URI(url)
      val domain: String = uri.getHost

      if (domain.startsWith("www.")) domain.substring(4) else domain
    }

  def findElement(by: By)(implicit trace: Trace): IO[WebDriverException, WebElement]
  def findElements(by: By)(implicit trace: Trace): IO[WebDriverException, List[WebElement]]
  def hasElement(by: By)(implicit trace: Trace): IO[WebDriverException, Boolean] = findElements(by).map(_.nonEmpty)

  def close(implicit trace: Trace): IO[WebDriverException, Unit]

  def underlying: SeleniumWebDriver

  def navigate: Navigate
  def manage: Manage
}

object WebDriver {

  /** Accessor functions */

  private def access[A](f: WebDriver => IO[WebDriverException, A]): ZIO[WebDriver, WebDriverException, A] =
    ZIO.serviceWithZIO[WebDriver](f)

  def get(url: URL)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit]    = Navigate.to(url)
  def get(url: String)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] = Navigate.to(url)

  def getCurrentUrl(implicit trace: Trace): ZIO[WebDriver, WebDriverException, String] = access(_.getCurrentUrl)
  def getPageSource(implicit trace: Trace): ZIO[WebDriver, WebDriverException, String] = access(_.getPageSource)
  def getDomain(implicit trace: Trace): ZIO[WebDriver, WebDriverException, String]     = access(_.getDomain)
  def getTitle(implicit trace: Trace): ZIO[WebDriver, WebDriverException, String]      = access(_.getTitle)

  def findElement(by: By)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, WebElement] =
    access(_.findElement(by))

  def findElements(by: By)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, List[WebElement]] =
    access(_.findElements(by))

  def hasElement(by: By)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Boolean] = access(_.hasElement(by))

  def close(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] = access(_.close)

  def underlying(implicit trace: Trace): ZIO[WebDriver, Nothing, SeleniumWebDriver] =
    ZIO.service[WebDriver].map(_.underlying)

  object Navigate {
    def to(url: URL)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit]       = access(_.navigate.to(url))
    def to(string: String)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] = to(new URL(string))

    def back(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit]    = access(_.navigate.back)
    def forward(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] = access(_.navigate.forward)
    def refresh(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] = access(_.navigate.refresh)
  }

  object Manage {

    def addCookie(cookie: Cookie)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.addCookie(cookie))

    def addCookie(key: String, value: String)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.addCookie(key, value))

    def addCookies(cookies: Seq[Cookie])(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.addCookies(cookies))

    def getCookieNamed(key: String)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Option[Cookie]] =
      access(_.manage.getCookieNamed(key))

    def getAllCookies(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Set[Cookie]] =
      access(_.manage.getAllCookies)

    def deleteCookie(cookie: Cookie)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.deleteCookie(cookie))

    def deleteCookieNamed(key: String)(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.deleteCookieNamed(key))

    def deleteAllCookies(implicit trace: Trace): ZIO[WebDriver, WebDriverException, Unit] =
      access(_.manage.deleteAllCookies)
  }

  /** Live implementation */

  final case class WebDriverLive(driver: SeleniumWebDriver) extends WebDriver {

    override def getCurrentUrl(implicit trace: Trace): IO[WebDriverException, String] = attempt(driver.getCurrentUrl)
    override def getPageSource(implicit trace: Trace): IO[WebDriverException, String] = attempt(driver.getPageSource)
    override def getTitle(implicit trace: Trace): IO[WebDriverException, String]      = attempt(driver.getTitle)

    override def findElement(by: By)(implicit trace: Trace): IO[WebDriverException, WebElement] =
      attemptWebElement(driver.findElement(by))

    override def findElements(by: By)(implicit trace: Trace): IO[WebDriverException, List[WebElement]] =
      attemptWebElements(driver.findElements(by))

    override def close(implicit trace: Trace): IO[WebDriverException, Unit] = attempt(driver.close())

    override def navigate: Navigate =
      new Navigate {

        override def to(url: URL)(implicit trace: Trace): IO[WebDriverException, Unit] =
          attempt(driver.navigate().to(url))
        override def back(implicit trace: Trace): IO[WebDriverException, Unit]    = attempt(driver.navigate().back())
        override def forward(implicit trace: Trace): IO[WebDriverException, Unit] = attempt(driver.navigate().forward())
        override def refresh(implicit trace: Trace): IO[WebDriverException, Unit] = attempt(driver.navigate().refresh())
      }

    override def manage: Manage =
      new Manage {

        override def addCookie(cookie: Cookie)(implicit trace: Trace): IO[WebDriverException, Unit] =
          attempt(driver.manage().addCookie(cookie))

        override def getCookieNamed(key: String)(implicit trace: Trace): IO[WebDriverException, Option[Cookie]] =
          attempt(driver.manage.getCookieNamed(key)).map(Option.apply)

        override def getAllCookies(implicit trace: Trace): IO[WebDriverException, Set[Cookie]] =
          attempt(driver.manage.getCookies.asScala.toList.toSet)

        override def deleteCookie(cookie: Cookie)(implicit trace: Trace): IO[WebDriverException, Unit] =
          attempt(driver.manage.deleteCookie(cookie))

        override def deleteCookieNamed(key: String)(implicit trace: Trace): IO[WebDriverException, Unit] =
          attempt(driver.manage.deleteCookieNamed(key))

        override def deleteAllCookies(implicit trace: Trace): IO[WebDriverException, Unit] =
          attempt(driver.manage.deleteAllCookies())
      }

    override def underlying: SeleniumWebDriver = driver
  }

  def layer(webdriver: SeleniumWebDriver): Layer[WebDriverException, WebDriver] = {
    val acquire = ZIO.attempt(webdriver).refineToOrDie[WebDriverException].map(WebDriverLive)
    ZLayer.scoped(ZIO.acquireRelease(acquire)(_.close.orDie))
  }

}
