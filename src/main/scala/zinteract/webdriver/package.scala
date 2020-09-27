package zinteract

import zio.{Has, UIO, URIO, ZIO, ZLayer}
import org.openqa.selenium.{WebDriver => SeleniumWebDriver}
import org.openqa.selenium.chrome.ChromeDriver

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
        ZLayer.fromAcquireRelease(for {
          properties <- properties()
          driver <-
            ZIO
              .effect({
                properties.foreach(_.insert());
                val driver: SeleniumWebDriver = new ChromeDriver()
                driver
              })
        } yield driver)(driver => UIO(driver.quit()))

      def properties(): URIO[Properties, List[Property]] = ZIO.service
    }
  }
}
