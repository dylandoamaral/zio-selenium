package zinteract

import com.gargoylesoftware.htmlunit.WebClient
import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import zinteract.webdriver.WebDriver.Service.webdriver
import zio.ZLayer

class TestDriver(cssEnabled: Boolean, jsEnabled: Boolean) extends HtmlUnitDriver(jsEnabled) {
  override def modifyWebClient(client: WebClient): WebClient = {
    client.getOptions.setCssEnabled(cssEnabled)
    client.getOptions.setJavaScriptEnabled(jsEnabled)
    client
  }
}

object TestDriver {
  def testDriver(cssEnabled: Boolean, jsEnabled: Boolean): ZLayer[Any, Throwable, WebDriver] =
    webdriver(new TestDriver(cssEnabled, jsEnabled))

  def testLayer(cssEnabled: Boolean = false, jsEnabled: Boolean = false): ZLayer[Any, Throwable, WebDriver] =
    TestDriver.testDriver(cssEnabled, jsEnabled)
}
