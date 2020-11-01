package zinteract.webdriver

import zio.{Has, Task, UIO, ZIO, ZLayer}

import ChromeBlueprintOps.ChromeBlueprint

import org.openqa.selenium.{WebDriver => SeleniumWebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

sealed trait Builder[O, D] {
  def buildOptions: Task[O]
  def buildDriver: Task[D]
  def buildLayer: ZLayer[Any, Throwable, Has[SeleniumWebDriver]] =
    ZLayer.fromAcquireRelease(buildDriver.map(_.asInstanceOf[SeleniumWebDriver]))(driver => UIO(driver.quit()))
}

case class ChromeBuilder(path: String, blueprint: ChromeBlueprint = ChromeBlueprintOps.default)
    extends Builder[ChromeOptions, ChromeDriver] {

  def buildOptions: Task[ChromeOptions] =
    for {
      options <- ZIO.effect(new ChromeOptions())
      _       <- blueprint.link(options)
    } yield options

  def buildDriver: Task[ChromeDriver] =
    for {
      _       <- ZIO.effect(System.setProperty("webdriver.chrome.driver", path))
      options <- this.buildOptions
      driver  <- ZIO.effect(new ChromeDriver(options))
    } yield driver
}
