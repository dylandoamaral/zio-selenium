package zinteract.test

import zinteract.webdriver.WebDriver.Service.webdriver

import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.gargoylesoftware.htmlunit.WebClient

class TestDriver(cssEnabled: Boolean, jsEnabled: Boolean) extends HtmlUnitDriver(jsEnabled) {
  override def modifyWebClient(client: WebClient): WebClient = {
    client.getOptions().setCssEnabled(cssEnabled);
    client.getOptions().setJavaScriptEnabled(jsEnabled);
    client
  }
}

object TestDriver {
  def testDriver(cssEnabled: Boolean, jsEnabled: Boolean) =
    webdriver(new TestDriver(cssEnabled, jsEnabled))

  def testLayer(cssEnabled: Boolean = false, jsEnabled: Boolean = false) =
    TestDriver.testDriver(cssEnabled, jsEnabled)
}
