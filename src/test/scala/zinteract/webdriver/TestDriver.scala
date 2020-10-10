package zinteract.test

import zio.ZLayer

import zinteract.webdriver.Property
import zinteract.session.Session

import org.openqa.selenium.WebDriver
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
    ZLayer.succeed(List[Property]()) >>> zinteract.webdriver.WebDriver.Service
      .webdriver(new TestDriver(cssEnabled, jsEnabled))

  def testLayer(cssEnabled: Boolean = false, jsEnabled: Boolean = false) =
    TestDriver.testDriver(cssEnabled, jsEnabled) >>> Session.Service.live
}
