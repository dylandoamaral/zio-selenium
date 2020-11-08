package zinteract.webdriver

import zio.{Task, UIO, ZIO, ZLayer}

import ChromeBlueprintOps.ChromeBlueprint
import FirefoxBlueprintOps.FirefoxBlueprint
import zinteract.session.Session

import org.openqa.selenium.{WebDriver => SeleniumWebDriver}
import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}

/**
  * The builder is a tool to describe a webdriver
  * thanks to its blueprint.
  */
sealed trait Builder[Options, Driver] {

  /**
    * Build the options configuration from the builder description.
    */
  def buildOptions: Task[Options]

  /**
    * Build the WebDriver from the builder description.
    */
  def buildDriver: Task[Driver]

  /**
    * Build the ZLayer from the builder description.
    */
  def buildLayer: ZLayer[Any, Throwable, Session] =
    ZLayer.fromAcquireRelease(buildDriver.map(_.asInstanceOf[SeleniumWebDriver]))(driver =>
      UIO(driver.quit())
    ) >>> Session.Service.live
}

/**
  * A builder specific to the chromedriver that uses chrome
  * options for blueprint.
  */
case class ChromeBuilder(path: String, blueprint: ChromeBlueprint = ChromeBlueprintOps.default)
    extends Builder[ChromeOptions, ChromeDriver] {

  /**
    * Returns a builder using the new path.
    */
  def at(path: String): ChromeBuilder = this.copy(path = path)

  /**
    * Operator alias for `at`.
    */
  def >(path: String): ChromeBuilder = at(path)

  /**
    * Returns a builder using the new blueprint.
    */
  def using(blueprint: ChromeBlueprint): ChromeBuilder = this.copy(blueprint = blueprint)

  /**
    * Operator alias for `>>`.
    */
  def >>(blueprint: ChromeBlueprint): ChromeBuilder = using(blueprint)

  /**
    * Builds a ChromeOptions by applying the blueprint.
    */
  def buildOptions: Task[ChromeOptions] =
    for {
      options <- ZIO.effect(new ChromeOptions())
      _       <- blueprint.link(options)
    } yield options

  /**
    * Builds a ChromeDriver by applying the blueprint.
    */
  def buildDriver: Task[ChromeDriver] =
    for {
      _       <- ZIO.effect(System.setProperty("webdriver.chrome.driver", path))
      options <- this.buildOptions
      driver  <- ZIO.effect(new ChromeDriver(options))
    } yield driver
}

/**
  * A builder specific to the gechodriver that uses firefox
  * options for blueprint.
  */
case class FirefoxBuilder(path: String, blueprint: FirefoxBlueprint = FirefoxBlueprintOps.default)
    extends Builder[FirefoxOptions, FirefoxDriver] {

  /**
    * Returns a builder using the new path.
    */
  def at(path: String): FirefoxBuilder = this.copy(path = path)

  /**
    * Operator alias for `at`.
    */
  def >(path: String): FirefoxBuilder = at(path)

  /**
    * Returns a builder using the new blueprint.
    */
  def using(blueprint: FirefoxBlueprint): FirefoxBuilder = this.copy(blueprint = blueprint)

  /**
    * Operator alias for `>>`.
    */
  def >>(blueprint: FirefoxBlueprint): FirefoxBuilder = using(blueprint)

  /**
    * Builds a ChromeOptions by applying the blueprint.
    */
  def buildOptions: Task[FirefoxOptions] =
    for {
      options <- ZIO.effect(new FirefoxOptions())
      _       <- blueprint.link(options)
    } yield options

  /**
    * Builds a ChromeDriver by applying the blueprint.
    */
  def buildDriver: Task[FirefoxDriver] =
    for {
      _       <- ZIO.effect(System.setProperty("webdriver.gecko.driver", path))
      options <- this.buildOptions
      driver  <- ZIO.effect(new FirefoxDriver(options))
    } yield driver
}

object BuilderOps {

  /**
    * Create a unit chrome builder.
    */
  def chrome: ChromeBuilder = ChromeBuilder("")

  /**
    * Create a unit firefox builder.
    */
  def firefox: FirefoxBuilder = FirefoxBuilder("")
}
