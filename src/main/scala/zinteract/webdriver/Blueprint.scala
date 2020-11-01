package zinteract.webdriver

import zio.Task

import org.openqa.selenium.{MutableCapabilities, PageLoadStrategy, Proxy}
import org.openqa.selenium.chrome.ChromeOptions

import scala.jdk.CollectionConverters._
import java.io.File

case class Blueprint[A](link: A => Task[Unit]) {
  def <>[B <: A](that: Blueprint[B]): Blueprint[B] = Blueprint((options: B) => this.link(options) *> that.link(options))

  def and[B <: A](that: Blueprint[B]): Blueprint[B] = this <> that
}

object CommonBlueprintOps {
  type CommonBlueprint = Blueprint[MutableCapabilities]

  def unit: CommonBlueprint = Blueprint[MutableCapabilities](_ => Task.succeed())

  def setCapability(key: String, value: Object): CommonBlueprint =
    Blueprint(options => Task.effect(options.setCapability(key, value)))
}

object ChromeBlueprintOps {
  type ChromeBlueprint = Blueprint[ChromeOptions]

  val default: ChromeBlueprint =
    setLoadPageStrategy(PageLoadStrategy.NORMAL)

  def addArguments(args: List[String]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addArguments(args: _*)))

  def addArgument(arg: String): ChromeBlueprint =
    addArguments(List(arg))

  def fullscreen: ChromeBlueprint =
    addArgument("--start-fullscreen")

  def noExtensions: ChromeBlueprint =
    addArgument("--disable-extensions")

  def noPopupBlocking: ChromeBlueprint =
    addArgument("--disable-popup-blocking")

  def noGpu: ChromeBlueprint =
    addArgument("--disable-gpu")

  def setLoadPageStrategy(strategy: PageLoadStrategy): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setPageLoadStrategy(strategy)))

  def setHeadless(bool: Boolean): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setHeadless(bool)))

  def headless: ChromeBlueprint = setHeadless(true)

  def headfull: ChromeBlueprint = setHeadless(false)

  def setBinary(path: String): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setBinary(path)))

  def setBinary(path: File): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setBinary(path)))

  def addExtension(extension: File): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addExtensions(extension)))

  def addExtensions(extensions: List[File]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addExtensions(extensions.asJava)))

  def addEncodedExtension(encoded: String): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addEncodedExtensions(encoded)))

  def addEncodedExtensions(encoded: List[String]): ChromeBlueprint =
    Blueprint(options => Task.effect(options.addEncodedExtensions(encoded.asJava)))

  def setProxy(proxy: Proxy): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setProxy(proxy)))
}
