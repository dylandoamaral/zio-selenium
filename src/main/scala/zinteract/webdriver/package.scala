package zinteract

import zio.clock.Clock
import zio.duration.{Duration, DurationOps}
import zio.{Has, RIO, UIO, ZIO, ZLayer}

import zinteract.context._

import org.openqa.selenium.support.ui.{ExpectedConditions, FluentWait, Wait}
import org.openqa.selenium.{Alert, By, Cookie, NoSuchCookieException, NoSuchElementException, WebDriver => SeleniumWebDriver, WebDriverException, WebElement}

import java.net.URI
import scala.jdk.CollectionConverters._

/** Provides some tools to use Selenium WebDriver has
  * ZLayer that are mandatory to use when dealing with
  * Zinteract.
  */
package object webdriver {

  type WebDriver = Has[SeleniumWebDriver]

  object WebDriver {
    object Service {

      /** Returns a WebDriver has a ZLayer.
        */
      def webdriver(webDriver: => SeleniumWebDriver): ZLayer[Any, Throwable, WebDriver] =
        ZLayer.fromAcquireRelease(ZIO.effect({ webDriver.asInstanceOf[SeleniumWebDriver] }))(driver =>
          UIO(driver.quit())
        )
    }
  }

  object navigate {

    /** Loads a new web page in the current browser window. This is done using an HTTP GET operation,
      * and the method will block until the load is complete. This will follow redirects issued either
      * by the server or as a meta-redirect from within the returned HTML. Should a meta-redirect
      * "rest" for any duration of time, it is best to wait until this timeout is over, since should
      * the underlying page change whilst your test is executing the results of future calls against
      * this interface will be against the freshly loaded page.
      */
    def to(url: String): ZIO[WebDriver, WebDriverException, Unit] =
      effect(_.navigate.to(url)).refineToOrDie[WebDriverException]

    /** Moves back a single "item" in the browser's history.
      */
    def back: ZIO[WebDriver, Throwable, Unit] =
      effect(_.navigate.back)

    /** Moves a single "item" forward in the browser's history. Does nothing if we are on the latest
      * page viewed.
      */
    def forward: ZIO[WebDriver, Throwable, Unit] =
      effect(_.navigate.forward)

    /** Refreshs the current page
      */
    def refresh: ZIO[WebDriver, Throwable, Unit] =
      effect(_.navigate.refresh)
  }

  object manage {

    /** Adds a specific cookie. If the cookie's domain name is left blank, it is assumed that the
      * cookie is meant for the domain of the current document.
      */
    def addCookie(cookie: Cookie): ZIO[WebDriver, Throwable, Unit] =
      effect(_.manage.addCookie(cookie))

    /** Adds a specific cookie only using a key and a name. The domain is the domain of the
      * current document
      */
    def addCookie(key: String, value: String): ZIO[WebDriver, Throwable, Unit] =
      addCookie(new Cookie(key, value))

    /** Gets a cookie with a given name.
      */
    def getCookieNamed(key: String): ZIO[WebDriver, Throwable, Cookie] =
      underlying.flatMap(_.manage.getCookieNamed(key) match {
        case null   => ZIO.fail(new NoSuchCookieException(s"Cookie named '$key' doesn't exist"))
        case cookie => ZIO.succeed(cookie)
      })

    /** Gets all the cookies for the current domain. This is the equivalent of calling
      * "document.cookie" and parsing the result
      */
    def getAllCookies: ZIO[WebDriver, Throwable, List[Cookie]] =
      effect(_.manage.getCookies.asScala.toList)

    /** Deletes a cookie from the browser's "cookie jar". The domain of the cookie will be ignored.
      */
    def deleteCookie(cookie: Cookie): ZIO[WebDriver, Throwable, Unit] =
      effect(_.manage.deleteCookie(cookie))

    /** Deletes a cookie from the browser's "cookie jar". The domain of the cookie will be ignored.
      */
    def deleteCookieNamed(key: String): ZIO[WebDriver, Throwable, Unit] =
      effect(_.manage.deleteCookieNamed(key))

    /** Deletes all the cookies for the current domain.
      */
    def deleteAllCookies: ZIO[WebDriver, Throwable, Unit] =
      effect(_.manage.deleteAllCookies)
  }

  /** Get the underlying Selenium WebDriver.
    */
  def underlying: RIO[WebDriver, SeleniumWebDriver] =
    ZIO.access(_.get)

  /** Allow side effects to fail purely inside accessor function
    */
  def effect[A](effect: SeleniumWebDriver => A): ZIO[WebDriver, Throwable, A] =
    underlying.flatMap(webdriver => ZIO.effect(effect(webdriver)))

  /** Alias for `navigate.to`
    */
  def get(url: String): ZIO[WebDriver, WebDriverException, Unit] =
    navigate.to(url)

  /** Alias for `navigate.to`
    */
  def link(url: String): ZIO[WebDriver, WebDriverException, Unit] =
    navigate.to(url)

  /** Alias for `navigate.to`
    */
  def goto(url: String): ZIO[WebDriver, WebDriverException, Unit] =
    navigate.to(url)

  /** Gets a string representing the current URL that the browser is looking at.
    */
  def getCurrentUrl: ZIO[WebDriver, Throwable, String] =
    effect(_.getCurrentUrl)

  /** Alias for `getCurrentUrl`
    */
  def url: ZIO[WebDriver, Throwable, String] =
    getCurrentUrl

  /** Gets a string representing the current domain that the browser is looking at.
    */
  def getDomain: ZIO[WebDriver, Throwable, String] =
    url.map(url => {
      val uri: URI       = new URI(url)
      val domain: String = uri.getHost();

      if (domain.startsWith("www.")) domain.substring(4) else domain
    })

  /** Alias for `getDomain`
    */
  def domain: ZIO[WebDriver, Throwable, String] =
    getDomain

  /** The title of the current page, with leading and trailing whitespace stripped.
    */
  def getTitle: ZIO[WebDriver, Throwable, String] =
    effect(_.getTitle)

  /** Alias for `getTitle`
    */
  def title: ZIO[WebDriver, Throwable, String] =
    getTitle

  /** Returns the source of the page
    */
  def getPageSource: RIO[WebDriver, String] =
    effect(_.getPageSource)

  /** Alias for `getPageSource`
    */
  def source: RIO[WebDriver, String] =
    getPageSource

  /** Finds the first WebElement using the given method.
    */
  def findElement(by: By)(implicit wait: WaitConfig = None): ZIO[WebDriver with Clock, Throwable, WebElement] =
    underlying.flatMap(findElementFrom(_)(by)(wait))

  /** Finds all WebElements using the given method.
    */
  def findElements(by: By)(implicit wait: WaitConfig = None): RIO[WebDriver with Clock, List[WebElement]] =
    underlying.flatMap(findElementsFrom(_)(by)(wait))

  /** Checks if the given method find an element.
    */
  def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[WebDriver with Clock, Boolean] =
    underlying.flatMap(hasElementFrom(_)(by)(wait))

  /** Returns a fluent wait
    */
  def defineFluentWaiter(polling: Duration, timeout: Duration): RIO[WebDriver, Fluent] =
    effect(webdriver =>
      Fluent(
        new FluentWait[SeleniumWebDriver](webdriver)
          .pollingEvery(polling.asJava)
          .withTimeout(timeout.asJava)
          .ignoring(classOf[NoSuchElementException])
      )
    )

  /** Gets the current alert using a Selenium wait.
    */
  def getAlert(wait: Wait[SeleniumWebDriver]): ZIO[WebDriver, Throwable, Alert] =
    ZIO.effect(wait.until(ExpectedConditions.alertIsPresent))

  /** Gets the current alert by providing a polling and timeout duration.
    */
  def getAlert(polling: Duration, timeout: Duration): ZIO[WebDriver, Throwable, Alert] =
    defineFluentWaiter(polling, timeout).flatMap(wait => getAlert(wait.waiter))
}
