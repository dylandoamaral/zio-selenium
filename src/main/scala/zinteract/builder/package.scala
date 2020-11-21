package zinteract

import zio.{Task, UIO, ZIO, ZLayer}

import zinteract.webdriver.WebDriver

import org.openqa.selenium.chrome.{ChromeDriver, ChromeOptions}
import org.openqa.selenium.firefox.{FirefoxDriver, FirefoxOptions}
import org.openqa.selenium.{WebDriver => SeleniumWebDriver}

/** Builder provides methods to create driver easily and purely.
  */
package object builder {

  /** The builder is a tool to describe a webdriver
    * thanks to its blueprint.
    */
  sealed trait Builder[Options, Driver] {

    /** Build the options configuration from the builder description.
      */
    def buildOptions: Task[Options]

    /** Build the WebDriver from the builder description.
      */
    def buildDriver: Task[Driver]

    /** Build the ZLayer from the builder description.
      */
    def buildLayer: ZLayer[Any, Throwable, WebDriver] =
      ZLayer.fromAcquireRelease(buildDriver.map(_.asInstanceOf[SeleniumWebDriver]))(driver => UIO(driver.quit()))
  }

  /** A general Builder for Remote Web Driver
    */
  sealed case class RemoteBuilder[Options, Driver](
      path: Option[String],
      blueprint: Blueprint[Options],
      pathProperty: String,
      createOptions: () => Options,
      createDriver: Options => Driver
  ) extends Builder[Options, Driver] {

    /** Returns a builder using the new path.
      */
    def at(path: String): RemoteBuilder[Options, Driver] = this.copy(path = Some(path))

    /** Operator alias for `at`.
      */
    def >(path: String): RemoteBuilder[Options, Driver] = at(path)

    /** Returns a builder using the new blueprint.
      */
    def using(blueprint: Blueprint[Options]): RemoteBuilder[Options, Driver] =
      this.copy(blueprint = blueprint)

    /** Operator alias for `>>`.
      */
    def >>(blueprint: Blueprint[Options]): RemoteBuilder[Options, Driver] = using(blueprint)

    /** Builds Options by applying the blueprint.
      */
    def buildOptions: Task[Options] =
      for {
        options <- ZIO.effect(createOptions())
        _       <- blueprint.link(options)
      } yield options

    /** Builds a Driver by applying the blueprint.
      */
    def buildDriver: Task[Driver] =
      for {
        _ <- path match {
          case None       => ZIO.succeed()
          case Some(path) => ZIO.effect(System.setProperty(pathProperty, path))

        }
        options <- this.buildOptions
        driver  <- ZIO.effect(createDriver(options))
      } yield driver
  }

  type ChromeBuilder  = RemoteBuilder[ChromeOptions, ChromeDriver]
  type FirefoxBuilder = RemoteBuilder[FirefoxOptions, FirefoxDriver]

  /** Create an unit chrome builder.
    */
  def chrome: ChromeBuilder =
    RemoteBuilder(
      None,
      ChromeBlueprint.default,
      "webdriver.chrome.driver",
      () => new ChromeOptions(),
      options => new ChromeDriver(options)
    )

  /** Create an unit firefox builder.
    */
  def firefox: FirefoxBuilder =
    RemoteBuilder(
      None,
      FirefoxBlueprint.default,
      "webdriver.gecko.driver",
      () => new FirefoxOptions(),
      options => new FirefoxDriver(options)
    )
}
