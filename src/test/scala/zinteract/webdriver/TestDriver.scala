package zinteract

import zio.ZLayer

import zinteract.webdriver.Property
import zinteract.surfer.Surfer

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.gargoylesoftware.htmlunit.WebClient

class TestDriver extends HtmlUnitDriver(true) {
  override def modifyWebClient(client: WebClient): WebClient = {
    client.getOptions().setCssEnabled(false);
    client.getOptions().setJavaScriptEnabled(true);
    client
  }
}

object TestDriver {
  val testDriver =
    ZLayer.succeed(List[Property]()) >>> zinteract.webdriver.WebDriver.Service.webdriver(new TestDriver())

  val testLayer = TestDriver.testDriver >>> Surfer.Service.live
}
