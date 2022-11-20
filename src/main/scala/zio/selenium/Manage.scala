package zio.selenium

import zio._

import org.openqa.selenium.{Cookie, WebDriverException}

trait Manage {

  /**
   * Add a specific cookie. If the cookie's domain name is left blank,
   * it is assumed that the cookie is meant for the domain of the
   * current document. <p> See <a
   * href="https://w3c.github.io/webdriver/#add-cookie">W3C WebDriver
   * specification</a> for more details.
   *
   * @param cookie
   *   The cookie to add.
   */
  def addCookie(cookie: Cookie): IO[WebDriverException, Unit]

  /**
   * Add a specific cookie. If the cookie's domain name is left blank,
   * it is assumed that the cookie is meant for the domain of the
   * current document. <p> See <a
   * href="https://w3c.github.io/webdriver/#add-cookie">W3C WebDriver
   * specification</a> for more details.
   *
   * @param cookie
   *   The cookie to add.
   */
  def addCookie(key: String, value: String): IO[WebDriverException, Unit] = addCookie(new Cookie(key, value))

  /**
   * Add cookies. For each cookie, if the cookie's domain name is left
   * blank, it is assumed that the cookie is meant for the domain of the
   * current document. <p> See <a
   * href="https://w3c.github.io/webdriver/#add-cookie">W3C WebDriver
   * specification</a> for more details.
   *
   * @param cookies
   *   The cookies to add.
   */
  def addCookies(cookies: Seq[Cookie]): IO[WebDriverException, Unit] = ZIO.foreachDiscard(cookies)(addCookie)

  /**
   * Get a cookie with a given name. <p> See <a
   * href="https://w3c.github.io/webdriver/#get-named-cookie">W3C
   * WebDriver specification</a> for more details.
   *
   * @param name
   *   the name of the cookie
   * @return
   *   the cookie, or null if no cookie with the given name is present
   */
  def getCookieNamed(key: String): IO[WebDriverException, Option[Cookie]]

  /**
   * Get all the cookies for the current domain. <p> See <a
   * href="https://w3c.github.io/webdriver/#get-all-cookies">W3C
   * WebDriver specification</a> for more details.
   *
   * @return
   *   A Set of cookies for the current domain.
   */
  def getAllCookies: IO[WebDriverException, Set[Cookie]]

  /**
   * Delete a cookie from the browser's "cookie jar". The domain of the
   * cookie will be ignored.
   *
   * @param cookie
   *   The cookies to delete.
   */
  def deleteCookie(cookie: Cookie): IO[WebDriverException, Unit]

  /**
   * Delete the named cookie from the current domain. This is equivalent
   * to setting the named cookie's expiry date to some time in the past.
   * <p> See <a
   * href="https://w3c.github.io/webdriver/#delete-cookie">W3C WebDriver
   * specification</a> for more details.
   *
   * @param name
   *   The name of the cookie to delete
   */
  def deleteCookieNamed(key: String): IO[WebDriverException, Unit]

  /**
   * Delete all the cookies for the current domain. <p> See <a
   * href="https://w3c.github.io/webdriver/#delete-all-cookies">W3C
   * WebDriver specification</a> for more details.
   */
  def deleteAllCookies: IO[WebDriverException, Unit]
}
