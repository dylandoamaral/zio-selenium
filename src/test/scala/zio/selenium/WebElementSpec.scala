package zio.selenium

import org.openqa.selenium.{By, WebDriverException}

import zio._
import zio.test._
import zio.test.TestAspect.sequential

object WebElementSpec extends SharedWebDriverSpec {

  val testPath: String = getClass.getResource("/ElementSpec.html").getPath
  val testWebsite      = s"file://$testPath"

  override def spec: Spec[WebDriver, WebDriverException] =
    suite("WebElement Spec")(
      test("WebElement such as input should be writable") {
        for {
          _      <- WebDriver.get(testWebsite)
          search <- WebDriver.findElement(By.id("input"))
          _      <- search.sendKeys("test")
          value  <- search.getAttribute("value")
        } yield assertTrue(value.contains("test"))
      },
      test("WebElement such as input should be cleanable") {
        for {
          _      <- WebDriver.get(testWebsite)
          search <- WebDriver.findElement(By.id("input"))
          _      <- search.sendKeys("test")
          _      <- search.clear
          value  <- search.getAttribute("value")
        } yield assertTrue(value.isEmpty)
      },
      test("WebElement such as button should be clickable") {
        for {
          _      <- WebDriver.get(testWebsite)
          button <- WebDriver.findElement(By.tagName("a"))
          _      <- button.click
          url    <- WebDriver.getCurrentUrl
        } yield assertTrue(url == "https://github.com/")
      },
      test("WebElement such as form should be submitable") {
        for {
          _    <- WebDriver.get(testWebsite)
          form <- WebDriver.findElement(By.tagName("form"))
          _    <- form.submit
          url  <- WebDriver.getCurrentUrl
        } yield assertTrue(url == "https://github.com/")
      },
      test("WebElement has attributes") {
        for {
          _     <- WebDriver.get(testWebsite)
          h1    <- WebDriver.findElement(By.id("test"))
          title <- h1.getAttribute("title")
        } yield assertTrue(title.contains("Test 1"))
      },
      test("WebElement can have a text") {
        for {
          _    <- WebDriver.get(testWebsite)
          h1   <- WebDriver.findElement(By.id("test"))
          text <- h1.getText
        } yield assertTrue(text.contains("Test 1"))
      },
      test("WebElement has css values") {
        for {
          _     <- WebDriver.get(testWebsite)
          h1    <- WebDriver.findElement(By.id("test"))
          value <- h1.getCssValue("color")
        } yield assertTrue(value.contains("rgba(0, 0, 255, 0.5)"))
      },
      test("WebElement should return None for unknown css property") {
        for {
          _     <- WebDriver.get(testWebsite)
          h1    <- WebDriver.findElement(By.id("test"))
          value <- h1.getCssValue("unknown")
        } yield assertTrue(value.isEmpty)
      },
      test("WebElement has a tag name") {
        for {
          _   <- WebDriver.get(testWebsite)
          h1  <- WebDriver.findElement(By.tagName("h1"))
          tag <- h1.getTagName
        } yield assertTrue(tag == "h1")
      },
      test("Element has a location, as size and a rect") {
        for {
          _        <- WebDriver.get(testWebsite)
          h1       <- WebDriver.findElement(By.tagName("h1"))
          location <- h1.getLocation
          size     <- h1.getSize
          rect     <- h1.getRect
        } yield assertTrue(location.getX == rect.getX) &&
          assertTrue(location.getY == rect.getY) &&
          assertTrue(size.getWidth == rect.getWidth) &&
          assertTrue(size.getHeight == rect.getHeight)
      },
      test("Element is unselected by default") {
        for {
          _        <- WebDriver.get(testWebsite)
          element  <- WebDriver.findElement(By.id("unselected"))
          selected <- element.isSelected
        } yield assertTrue(!selected)
      },
      test("Element can be selected") {
        for {
          _        <- WebDriver.get(testWebsite)
          element  <- WebDriver.findElement(By.id("selected"))
          selected <- element.isSelected
        } yield assertTrue(selected)

      },
      test("Element is enable by default") {
        for {
          _       <- WebDriver.get(testWebsite)
          element <- WebDriver.findElement(By.id("enable"))
          enabled <- element.isEnabled
        } yield assertTrue(enabled)
      },
      test("Element can be disable") {
        for {
          _       <- WebDriver.get(testWebsite)
          element <- WebDriver.findElement(By.id("disable"))
          enabled <- element.isEnabled
        } yield assertTrue(!enabled)
      },
      test("Element can find an element") {
        for {
          _    <- WebDriver.get(testWebsite)
          div  <- WebDriver.findElement(By.id("div"))
          p    <- div.findElement(By.tagName("p"))
          text <- p.getText
        } yield assertTrue(text == "Test 3")
      },
      test("Element can find several elements") {
        for {
          _     <- WebDriver.get(testWebsite)
          div   <- WebDriver.findElement(By.id("div"))
          ps    <- div.findElements(By.tagName("p"))
          texts <- ZIO.foreach(ps)(_.getText)
        } yield assertTrue(texts == List("Test 3"))
      },
      test("Element can check if an element exist") {
        for {
          _    <- WebDriver.get(testWebsite)
          div  <- WebDriver.findElement(By.id("div"))
          bool <- div.hasElement(By.tagName("p"))
        } yield assertTrue(bool)
      }
    ) @@ sequential
}
