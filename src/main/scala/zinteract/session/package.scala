package zinteract

import zio.{Has, IO, RIO, Task, UIO, ZIO, ZLayer}
import zio.clock.Clock
import zio.duration.Duration

import zinteract.webdriver.WebDriver
import zinteract.context._

import org.openqa.selenium.{
  Alert,
  By,
  Cookie,
  NoSuchElementException,
  NoSuchCookieException,
  WebElement,
  WebDriver => SeleniumWebDriver
}
import org.openqa.selenium.support.ui.{ExpectedConditions, FluentWait, Wait}
import java.net.URI

import scala.jdk.CollectionConverters._

/**
  * Session provides a way to interact purely with webdriver
  *
  * {{{
  * val effect = for {
  *    _       <- session.link("https://www.selenium.dev/documentation/en/")
  *    element <- session.findElement(By.id("the-selenium-browser-automation-project"))
  * } yield element
  *
  * app.provideCustomLayer(ChromeBuilder(pathToDriver).buildLayer >>> session.Session.Service.live)
  * }}}
  */
package object session {
  type Session = Has[Session.Service]

  object Session extends Serializable {

    /**
      * Operations avaible with the session layer
      */
    trait Service extends Serializable {
      def back: Task[Unit]
      def forward: Task[Unit]
      def refresh: Task[Unit]

      def link(url: String): Task[Unit]
      def url: UIO[String]
      def domain: UIO[String]
      def title: UIO[String]

      def findElement(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, NoSuchElementException, WebElement]
      def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]]
      def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean]

      def getWebdriver: UIO[SeleniumWebDriver]
      def getFluentWaiter(polling: Duration, timeout: Duration): UIO[Fluent]
      def getPageSource: UIO[String]

      def addCookie(cookie: Cookie): Task[Unit]
      def addCookie(key: String, value: String): Task[Unit]
      def getCookieNamed(key: String): IO[NoSuchCookieException, Cookie]
      def getAllCookies(): Task[List[Cookie]]
      def deleteCookie(cookie: Cookie): Task[Unit]
      def deleteCookieNamed(key: String): Task[Unit]
      def deleteAllCookies(): Task[Unit]

      def getAlert(wait: Wait[SeleniumWebDriver]): Task[Alert]

      def getAlert(polling: Duration, timeout: Duration): Task[Alert]
    }

    object Service {
      val live: ZLayer[WebDriver, Nothing, Session] =
        ZLayer.fromService(webdriver =>
          new Session.Service {
            def back: Task[Unit] =
              ZIO.effect(webdriver.navigate().back)

            def forward: Task[Unit] =
              ZIO.effect(webdriver.navigate().forward)

            def refresh: Task[Unit] =
              ZIO.effect(webdriver.navigate().refresh)

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

            def getPageSource: UIO[String] =
              ZIO.succeed(webdriver.getPageSource)

            def addCookie(cookie: Cookie): Task[Unit] =
              ZIO.effect(webdriver.manage().addCookie(cookie))

            def addCookie(key: String, value: String): Task[Unit] =
              this.addCookie(new Cookie(key, value))

            def getCookieNamed(key: String): IO[NoSuchCookieException, Cookie] =
              webdriver.manage().getCookieNamed(key) match {
                case null   => ZIO.fail(new NoSuchCookieException(s"Cookie named '$key' doesn't exist"))
                case cookie => ZIO.succeed(cookie)
              }

            def getAllCookies(): Task[List[Cookie]] =
              ZIO.effect(webdriver.manage().getCookies.asScala.toList)

            def deleteCookie(cookie: Cookie): Task[Unit] =
              ZIO.effect(webdriver.manage().deleteCookie(cookie))

            def deleteCookieNamed(key: String): Task[Unit] =
              ZIO.effect(webdriver.manage().deleteCookieNamed(key))

            def deleteAllCookies(): Task[Unit] =
              ZIO.effect(webdriver.manage().deleteAllCookies)

            def getAlert(wait: Wait[SeleniumWebDriver]): Task[Alert] =
              for {
                webdriver <- getWebdriver
                alert     <- ZIO.effect(wait.until(ExpectedConditions.alertIsPresent()))
              } yield alert

            def getAlert(polling: Duration, timeout: Duration): Task[Alert] =
              for {
                wait  <- getFluentWaiter(polling, timeout)
                alert <- getAlert(wait.waiter)
              } yield alert
          }
        )
    }
  }

  //accessor methods

  /**
    * Loads a new web page in the current browser window. This is done using an HTTP GET operation,
    * and the method will block until the load is complete. This will follow redirects issued either
    * by the server or as a meta-redirect from within the returned HTML. Should a meta-redirect
    * "rest" for any duration of time, it is best to wait until this timeout is over, since should
    * the underlying page change whilst your test is executing the results of future calls against
    * this interface will be against the freshly loaded page.
    */
  def link(url: String): ZIO[Session, Throwable, Unit] =
    ZIO.accessM(_.get.link(url))

  /**
    * Moves back a single "item" in the browser's history.
    */
  def back: RIO[Session, Unit] =
    ZIO.accessM(_.get.back)

  /**
    * Moves a single "item" forward in the browser's history. Does nothing if we are on the latest
    * page viewed.
    */
  def forward: RIO[Session, Unit] =
    ZIO.accessM(_.get.forward)

  /**
    * Refreshs the current page
    */
  def refresh: RIO[Session, Unit] =
    ZIO.accessM(_.get.refresh)

  /**
    * Gets a string representing the current URL that the browser is looking at.
    */
  def url: RIO[Session, String] =
    ZIO.accessM(_.get.url)

  /**
    * Gets a string representing the current domain that the browser is looking at.
    */
  def domain: RIO[Session, String] =
    ZIO.accessM(_.get.domain)

  /**
    *  The title of the current page, with leading and trailing whitespace stripped.
    */
  def title: RIO[Session, String] =
    ZIO.accessM(_.get.title)

  /**
    * Finds the first WebElement using the given method.
    */
  def findElement(
      by: By
  )(implicit wait: WaitConfig = None): ZIO[Session with Clock, NoSuchElementException, WebElement] =
    ZIO.accessM(_.get.findElement(by)(wait))

  /**
    * Finds all WebElements using the given method.
    */
  def findElements(by: By)(implicit wait: WaitConfig = None): RIO[Session with Clock, List[WebElement]] =
    ZIO.accessM(_.get.findElements(by)(wait))

  /**
    * Checks if the given method find an element.
    */
  def hasElement(by: By)(implicit wait: WaitConfig = None): RIO[Session with Clock, Boolean] =
    ZIO.accessM(_.get.hasElement(by)(wait))

  /**
    * Returns the current webdriver
    */
  def getWebdriver: RIO[Session, SeleniumWebDriver] =
    ZIO.accessM(_.get.getWebdriver)

  /**
    * Returns a fluent wait
    */
  def getFluentWaiter(polling: Duration, timeout: Duration): RIO[Session, Fluent] =
    ZIO.accessM(_.get.getFluentWaiter(timeout, polling))

  /**
    * Returns the source of the page
    */
  def getPageSource: RIO[Session, String] =
    ZIO.accessM(_.get.getPageSource)

  /**
    * Adds a specific cookie. If the cookie's domain name is left blank, it is assumed that the
    * cookie is meant for the domain of the current document.
    */
  def addCookie(cookie: Cookie): RIO[Session, Unit] =
    ZIO.accessM(_.get.addCookie(cookie))

  /**
    * Adds a specific cookie only using a key and a name. The domain is the domain of the
    * current document
    */
  def addCookie(key: String, value: String): RIO[Session, Unit] =
    ZIO.accessM(_.get.addCookie(key, value))

  /**
    * Gets a cookie with a given name.
    */
  def getCookieNamed(key: String): ZIO[Session, NoSuchCookieException, Cookie] =
    ZIO.accessM(_.get.getCookieNamed(key))

  /**
    * Gets all the cookies for the current domain. This is the equivalent of calling
    * "document.cookie" and parsing the result
    */
  def getAllCookies: RIO[Session, List[Cookie]] =
    ZIO.accessM(_.get.getAllCookies)

  /**
    * Deletes a cookie from the browser's "cookie jar". The domain of the cookie will be ignored.
    */
  def deleteCookie(cookie: Cookie): RIO[Session, Unit] =
    ZIO.accessM(_.get.deleteCookie(cookie))

  /**
    * Deletes a cookie from the browser's "cookie jar". The domain of the cookie will be ignored.
    */
  def deleteCookieNamed(key: String): RIO[Session, Unit] =
    ZIO.accessM(_.get.deleteCookieNamed(key))

  /**
    * Deletes all the cookies for the current domain.
    */
  def deleteAllCookies: RIO[Session, Unit] =
    ZIO.accessM(_.get.deleteAllCookies)

  /**
    * Gets the current alert using a Selenium wait.
    */
  def getAlert(wait: Wait[SeleniumWebDriver]): RIO[Session, Alert] =
    ZIO.accessM(_.get.getAlert(wait))

  /**
    * Gets the current alert by providing a polling and timeout duration.
    */
  def getAlert(polling: Duration, timeout: Duration): RIO[Session, Alert] =
    ZIO.accessM(_.get.getAlert(polling, timeout))
}
