package zio

import org.openqa.selenium.WebDriverException

package object selenium {

  def webdriverLayer(cssEnabled: Boolean): Layer[WebDriverException, WebDriver] =
    WebDriver.layer(new TestDriver(cssEnabled))
}
