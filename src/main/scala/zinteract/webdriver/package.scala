package zinteract

import zio.{Has, UIO, ZIO, ZLayer}

import org.openqa.selenium.{WebDriver => SeleniumWebDriver}

/**
  * Provides some tools to use Selenium WebDriver has
  * ZLayer that are mandatory to use when dealing with
  * Zinteract.
  */
package object webdriver {

  type WebDriver = Has[SeleniumWebDriver]

  object WebDriver {
    object Service {

      /**
        * Returns a WebDriver has a ZLayer.
        */
      def webdriver(webDriver: => SeleniumWebDriver): ZLayer[Any, Throwable, WebDriver] =
        ZLayer.fromAcquireRelease(for {
          driver <-
            ZIO
              .effect({
                val driver: SeleniumWebDriver = webDriver
                driver
              })
        } yield driver)(driver => UIO(driver.quit()))
    }
  }
}
