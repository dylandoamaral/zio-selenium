package zinteract.webdriver

import zio.Task

import org.openqa.selenium.{MutableCapabilities, PageLoadStrategy}
import org.openqa.selenium.chrome.ChromeOptions

case class Blueprint[A](link: A => Task[Unit]) {
  def <>[B >: A](that: Blueprint[B]) = Blueprint((a: A) => this.link(a) *> that.link(a))
}

object CommonBlueprintOps {
  type CommonBlueprint = Blueprint[MutableCapabilities]

  def unit: CommonBlueprint = Blueprint[MutableCapabilities](_ => Task.succeed())
}

object ChromeBlueprintOps {
  type ChromeBlueprint = Blueprint[ChromeOptions]

  val default: ChromeBlueprint =
    setLoadPageStrategy(PageLoadStrategy.NORMAL) <> headfull

  def setLoadPageStrategy(strategy: PageLoadStrategy): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setPageLoadStrategy(strategy)))

  def setHeadless(bool: Boolean): ChromeBlueprint =
    Blueprint(options => Task.effect(options.setHeadless(bool)))

  def headless: ChromeBlueprint = setHeadless(true)

  def headfull: ChromeBlueprint = setHeadless(false)
}
