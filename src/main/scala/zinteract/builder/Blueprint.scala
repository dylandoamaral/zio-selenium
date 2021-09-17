package zinteract.builder

import zio.Task

import org.openqa.selenium.chrome.ChromeOptions
import org.openqa.selenium.firefox.FirefoxOptions
import org.openqa.selenium.{MutableCapabilities, PageLoadStrategy}

/** The Blueprint describe a chaining of capabaility for a particular driver.
  */
case class Blueprint[A](link: A => Task[Unit]) {

  /** An operator to combine two blueprints into one.
    *
    * {{{
    * val blueprint: ChromeBlueprint = CommonBlueprintOps.unit and ChromeBlueprintOps.noGpu
    * }}}
    */
  def and[B <: A](that: Blueprint[B]): Blueprint[B] =
    Blueprint((options: B) => this.link(options) *> that.link(options))

  /** Operator alias for `and`.
    */
  def &&[B <: A](that: Blueprint[B]): Blueprint[B] = this and that
}

/** CommonBlueprint instances usable by any builder.
  */
object CommonBlueprint {
  type CommonBlueprint = Blueprint[MutableCapabilities]

  /** The unit blueprint that changes nothing to the options.
    */
  def unit: CommonBlueprint = Blueprint[MutableCapabilities](_ => Task.succeed())

  /** Set a new capability to the options.
    */
  def setCapability(key: String, value: Object): CommonBlueprint =
    Blueprint(options => Task.effect(options.setCapability(key, value)))
}

/** ChromeBlueprint instances usable by ChromeBuilder.
  */
object ChromeBlueprint {
  type ChromeBlueprint = Blueprint[ChromeOptions]

  /** Default configuration for the chrome blueprint.
    */
  val default: ChromeBlueprint = CommonBlueprint.unit.asInstanceOf[ChromeBlueprint]

  /** Adds command-line arguments to use when starting Chrome. Arguments with an associated value should be separated by
    * a '=' sign (e.g., ['start-maximized', 'user-data-dir=/tmp/temp_profile']).
    *
    * See [[https://peter.sh/experiments/chromium-command-line-switches/ here]] for a list of Chrome arguments.
    */
  def addArguments(args: List[String]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addArguments(args: _*)))

  /** Adds a command-line argument to use when starting Chrome. Arguments with an associated value should be separated
    * by a '=' sign (e.g., ['start-maximized', 'user-data-dir=/tmp/temp_profile']).
    *
    * See [[https://peter.sh/experiments/chromium-command-line-switches/ here]] for a list of Chrome arguments.
    */
  def addArgument(arg: String): ChromeBlueprint =
    addArguments(List(arg))

  /** Specifies if the browser should start in fullscreen mode, like if the user had pressed F11 right after startup.
    */
  def fullscreen: ChromeBlueprint =
    addArgument("--start-fullscreen")

  /** Disables extensions.
    */
  def noExtensions: ChromeBlueprint =
    addArgument("--disable-extensions")

  /** Disables pop-up blocking.
    */
  def noPopupBlocking: ChromeBlueprint =
    addArgument("--disable-popup-blocking")

  /** Disables GPU hardware acceleration. If software renderer is not in place, then the GPU process won't launch.
    */
  def noGpu: ChromeBlueprint =
    addArgument("--disable-gpu")

  /** Defines the current session’s page loading strategy.
    *
    * See [[https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/ here]] for more information.
    */
  def setLoadPageStrategy(strategy: PageLoadStrategy): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setPageLoadStrategy(strategy)))

  /** Chooses if you want to run in headless mode or not
    */
  def setHeadless(bool: Boolean): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setHeadless(bool)))

  /** Runs in headless mode, i.e., without a UI or display server dependencies.
    */
  def headless: ChromeBlueprint = setHeadless(true)
}

/** FirefoxBlueprint instances usable by FirefoxBuilder.
  */
object FirefoxBlueprint {
  type FirefoxBlueprint = Blueprint[FirefoxOptions]

  /** Default configuration for the firefox blueprint.
    */
  val default: FirefoxBlueprint = CommonBlueprint.unit.asInstanceOf[FirefoxBlueprint]

  /** Adds command-line arguments to use when starting Firefox. Arguments with an associated value should be separated
    * by a '=' sign (e.g., ['start-maximized', 'user-data-dir=/tmp/temp_profile']).
    *
    * See [[https://firefox-source-docs.mozilla.org/testing/geckodriver/Flags.html here]] for a list of Firefox
    * arguments.
    */
  def addArguments(args: List[String]): FirefoxBlueprint =
    Blueprint(options => Task.effect(options.addArguments(args: _*)))

  /** Adds a command-line argument to use when starting Firefox. Arguments with an associated value should be separated
    * by a '=' sign (e.g., ['start-maximized', 'user-data-dir=/tmp/temp_profile']).
    *
    * See [[https://firefox-source-docs.mozilla.org/testing/geckodriver/Flags.html here]] for a list of Firefox
    * arguments.
    */
  def addArgument(arg: String): FirefoxBlueprint =
    addArguments(List(arg))

  /** Defines the current session’s page loading strategy.
    *
    * See [[https://www.selenium.dev/documentation/en/webdriver/page_loading_strategy/ here]] for more information.
    */
  def setLoadPageStrategy(strategy: PageLoadStrategy): FirefoxBlueprint =
    Blueprint(options => Task.effect(options.setPageLoadStrategy(strategy)))

  /** Chooses if you want to run in headless mode or not
    */
  def setHeadless(bool: Boolean): FirefoxBlueprint =
    Blueprint(options => Task.effect(options.setHeadless(bool)))

  /** Runs in headless mode, i.e., without a UI or display server dependencies.
    */
  def headless: FirefoxBlueprint = setHeadless(true)
}
