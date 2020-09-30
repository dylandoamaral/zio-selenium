package zinteract

import zio.{Has, UIO, URIO, ZIO, ZLayer}

import org.openqa.selenium.{WebDriver => SeleniumWebDriver}
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.openqa.selenium.remote.DesiredCapabilities
import org.openqa.selenium.htmlunit.HtmlUnitDriver

package object webdriver {
  case class Property(key: String, value: String) {
    def insert(): Unit = { System.setProperty(key, value) }
  }

  type WebDriver  = Has[SeleniumWebDriver]
  type Properties = Has[List[Property]]

  object WebDriver {
    object Service {
      def chromeMinConfig(driverLocation: String): ZLayer[Any, Throwable, WebDriver] =
        ZLayer.succeed(List(Property("webdriver.chrome.driver", driverLocation))) >>> chrome

      val chrome: ZLayer[Properties, Throwable, WebDriver] =
        webdriver(new ChromeDriver())

      def firefoxMinConfig(driverLocation: String): ZLayer[Any, Throwable, WebDriver] =
        ZLayer.succeed(List(Property("webdriver.gecko.driver", driverLocation))) >>> firefox

      val firefox: ZLayer[Properties, Throwable, WebDriver] =
        webdriver({
          val capabilities = new DesiredCapabilities();
          capabilities.setCapability("marionatte", false);
          new FirefoxDriver(capabilities)
        })

      val htmlunitMinConfig: ZLayer[Any, Throwable, WebDriver] =
        ZLayer.succeed(List[Property]()) >>> htmlunit

      val htmlunit: ZLayer[Properties, Throwable, WebDriver] =
        webdriver(new HtmlUnitDriver())

      def webdriver(webDriver: => SeleniumWebDriver): ZLayer[Properties, Throwable, WebDriver] =
        ZLayer.fromAcquireRelease(for {
          properties <- properties()
          driver <-
            ZIO
              .effect({
                properties.foreach(_.insert());
                val driver: SeleniumWebDriver = webDriver
                driver
              })
        } yield driver)(driver => UIO(driver.quit()))

      def properties(): URIO[Properties, List[Property]] = ZIO.service
    }
  }
}
