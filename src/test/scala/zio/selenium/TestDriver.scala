package zio.selenium

import com.gargoylesoftware.htmlunit.WebClient
import org.openqa.selenium.htmlunit.HtmlUnitDriver

class TestDriver(cssEnabled: Boolean) extends HtmlUnitDriver {

  override def modifyWebClient(client: WebClient): WebClient = {
    client.getOptions.setCssEnabled(cssEnabled)
    client
  }
}
