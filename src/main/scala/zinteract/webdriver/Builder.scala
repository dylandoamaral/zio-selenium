package zinteract.webdriver

import zio.{Has, Task, UIO, ZIO, ZLayer}

import ChromeBlueprintOps.ChromeBlueprint

import org.openqa.selenium.{WebDriver => SeleniumWebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}

/**
  * The builder is a tool to describe a webdriver
  * thanks to its blueprint.
  */
sealed trait Builder[O, D] {

  /**
    * Build the options configuration from the builder description.
    */
  def buildOptions: Task[O]

  /**
    * Build the WebDriver from the builder description.
    */
  def buildDriver: Task[D]

  /**
    * Build the ZLayer from the builder description.
    */
  def buildLayer: ZLayer[Any, Throwable, Has[SeleniumWebDriver]] =
    ZLayer.fromAcquireRelease(buildDriver.map(_.asInstanceOf[SeleniumWebDriver]))(driver => UIO(driver.quit()))
}

/**
  * A builder specific to the chromedriver that uses chrome
  * options for blueprint.
  */
case class ChromeBuilder(path: String, blueprint: ChromeBlueprint = ChromeBlueprintOps.default)
    extends Builder[ChromeOptions, ChromeDriver] {

  /**
    * Build a ChromeOptions by applying the blueprint.
    */
  def buildOptions: Task[ChromeOptions] =
    for {
      options <- ZIO.effect(new ChromeOptions())
      _       <- blueprint.link(options)
    } yield options

  /**
    * Build a ChromeDriver by applying the blueprint.
    */
  def buildDriver: Task[ChromeDriver] =
    for {
      _       <- ZIO.effect(System.setProperty("webdriver.chrome.driver", path))
      options <- this.buildOptions
      driver  <- ZIO.effect(new ChromeDriver(options))
    } yield driver
}
