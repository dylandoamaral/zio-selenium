package zinteract

import zio.ZLayer

import zinteract.webdriver.Property

import org.openqa.selenium.WebDriver
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import com.gargoylesoftware.htmlunit.WebClient

class TestDriver extends HtmlUnitDriver {
  override def modifyWebClient(client: WebClient): WebClient = {
    client.getOptions().setCssEnabled(false);
    client.getOptions().setJavaScriptEnabled(false);
    client
  }
}

object TestDriver {
  val testDriver =
    ZLayer.succeed(List[Property]()) >>> zinteract.webdriver.WebDriver.Service.webdriver(new TestDriver())
}
