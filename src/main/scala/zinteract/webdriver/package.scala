package zinteract

import zio.{Has, UIO, ZIO, ZLayer}

import org.openqa.selenium.{WebDriver => SeleniumWebDriver}

package object webdriver {

  type WebDriver = Has[SeleniumWebDriver]

  object WebDriver {
    object Service {
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
