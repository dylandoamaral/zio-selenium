package zinteract.webdriver

import zio.Task

import org.openqa.selenium.{MutableCapabilities, PageLoadStrategy, Proxy}
import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions

import scala.jdk.CollectionConverters._
import java.io.File

/**
  * The Blueprint describe a chaining of capabaility for
  * a particular driver.
  */
case class Blueprint[A](link: A => Task[Unit]) {

  /**
    * An operator to combine two blueprints into one.
    *
    * {{{
    * val blueprint: ChromeBlueprint = CommonBlueprintOps.unit and ChromeBlueprintOps.noGpu
    * }}}
    */
  def and[B <: A](that: Blueprint[B]): Blueprint[B] =
    Blueprint((options: B) => this.link(options) *> that.link(options))

  /**
    * Operator alias for `and`.
    */
  def &&[B <: A](that: Blueprint[B]): Blueprint[B] = this and that
}

/**
  * CommonBlueprint instances usable by any builder.
  */
object CommonBlueprintOps {
  type CommonBlueprint = Blueprint[MutableCapabilities]

  /**
    * The unit blueprint that changes nothing to the options.
    */
  def unit: CommonBlueprint = Blueprint[MutableCapabilities](_ => Task.succeed())

  /**
    * Set a new capability to the options.
    */
  def setCapability(key: String, value: Object): CommonBlueprint =
    Blueprint(options => Task.effect(options.setCapability(key, value)))
}

/**
  * ChromeBlueprint instances usable by ChromeBuilder.
  */
object ChromeBlueprintOps {
  type ChromeBlueprint = Blueprint[ChromeOptions]

  /**
    * Default configuration for the chrome blueprint.
    */
  val default: ChromeBlueprint = CommonBlueprintOps.unit.asInstanceOf[ChromeBlueprint]

  /**
    * Adds command-line arguments to use when starting Chrome.
    * Arguments with an associated value should be separated by a '=' sign
    * (e.g., ['start-maximized', 'user-data-dir=/tmp/temp_profile']).
    *
    * See [[https://peter.sh/experiments/chromium-command-line-switches/ here]] for a list of Chrome arguments.
    */
  def addArguments(args: List[String]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addArguments(args: _*)))

  /**
    * Adds a command-line argument to use when starting Chrome.
    * Arguments with an associated value should be separated by a '=' sign
    * (e.g., ['start-maximized', 'user-data-dir=/tmp/temp_profile']).
    *
    * See [[https://peter.sh/experiments/chromium-command-line-switches/ here]] for a list of Chrome arguments.
    */
  def addArgument(arg: String): ChromeBlueprint =
    addArguments(List(arg))

  /**
    * Specifies if the browser should start in fullscreen mode, like if
    * the user had pressed F11 right after startup.
    */
  def fullscreen: ChromeBlueprint =
    addArgument("--start-fullscreen")

  /**
    * Disables extensions.
    */
  def noExtensions: ChromeBlueprint =
    addArgument("--disable-extensions")

  /**
    * Disables pop-up blocking.
    */
  def noPopupBlocking: ChromeBlueprint =
    addArgument("--disable-popup-blocking")

  /**
    * Disables GPU hardware acceleration. If software renderer is not in place,
    * then the GPU process won't launch.
    */
  def noGpu: ChromeBlueprint =
    addArgument("--disable-gpu")

  /**
    * Defines the current session’s page loading strategy.
    *
    * See [[https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/ here]] for more information.
    */
  def setLoadPageStrategy(strategy: PageLoadStrategy): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setPageLoadStrategy(strategy)))

  /**
    * Chooses if you want to run in headless mode or not
    */
  def setHeadless(bool: Boolean): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setHeadless(bool)))

  /**
    * Runs in headless mode, i.e., without a UI or display server dependencies.
    */
  def headless: ChromeBlueprint = setHeadless(true)

  /**
    * Doesn't run in headless mode.
    */
  def headfull: ChromeBlueprint = setHeadless(false)

  /**
    * Sets the path to the Chrome executable. This path should exist on the
    * machine which will launch Chrome. The path should either be absolute or
    * relative to the location of running ChromeDriver server.
    */
  def setBinary(path: String): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setBinary(path)))

  /**
    * Sets the path to the Chrome executable. This path should exist on the
    * machine which will launch Chrome. The path should either be absolute or
    * relative to the location of running ChromeDriver server.
    */
  def setBinary(path: File): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setBinary(path)))

  /**
    * Adds a new Chrome extension to install on browser startup. Each path should
    * specify a packed Chrome extension (CRX file).
    */
  def addExtension(extension: File): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addExtensions(extension)))

  /**
    * Adds new Chrome extensions to install on browser startup. Each path should
    * specify a packed Chrome extension (CRX file).
    */
  def addExtensions(extensions: List[File]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addExtensions(extensions.asJava)))

  /**
    * Adds a new Chrome extension to install on browser startup. Each string data should
    * specify a Base64 encoded string of packed Chrome extension (CRX file).
    */
  def addEncodedExtension(encoded: String): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addEncodedExtensions(encoded)))

  /**
    * Adds a new Chrome extension to install on browser startup. Each string data should
    * specify a Base64 encoded string of packed Chrome extension (CRX file).
    */
  def addEncodedExtensions(encoded: List[String]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addEncodedExtensions(encoded.asJava)))

  /**
    * Set a proxy to the ChromeDriver.
    */
  def setProxy(proxy: Proxy): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setProxy(proxy)))
}

/**
  * FirefoxBlueprint instances usable by FirefoxBuilder.
  */
object FirefoxBlueprintOps {
  type FirefoxBlueprint = Blueprint[FirefoxOptions]

  /**
    * Default configuration for the firefox blueprint.
    */
  val default: FirefoxBlueprint = CommonBlueprintOps.unit.asInstanceOf[FirefoxBlueprint]

  /**
    * Defines the current session’s page loading strategy.
    *
    * See [[https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/ here]] for more information.
    */
  def setLoadPageStrategy(strategy: PageLoadStrategy): FirefoxBlueprint =
    Blueprint(options => Task.effect(options.setPageLoadStrategy(strategy)))

  /**
    * Chooses if you want to run in headless mode or not
    */
  def setHeadless(bool: Boolean): FirefoxBlueprint =
    Blueprint(options => Task.effect(options.setHeadless(bool)))

  /**
    * Runs in headless mode, i.e., without a UI or display server dependencies.
    */
  def headless: FirefoxBlueprint = setHeadless(true)

  /**
    * Doesn't run in headless mode.
    */
  def headfull: FirefoxBlueprint = setHeadless(false)
}
