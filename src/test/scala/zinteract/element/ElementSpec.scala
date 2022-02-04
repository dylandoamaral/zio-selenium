package zinteract.element

import org.openqa.selenium.By
import zinteract.TestDriver.testLayer
import zinteract.webdriver
import zio.{ZEnv, ZIO}
import zio.test.Assertion._
import zio.test._

object ElementSpec extends DefaultRunnableSpec {
  val testPath: String = getClass.getResource("/ElementSpec.html").getPath
  val testWebsite      = s"file://$testPath"

  def suiteInteractElement: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Interact Element Spec")(
      test("Element such as input should be writable") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          search <- webdriver.findElement(By.id("input"))
          _      <- search.sendKeysM("test")
        } yield assert(search.getAttribute("value"))(equalTo("test"))

        effect.provideCustomLayer(testLayer())
      },
      test("Element such as input should be cleanable") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          search <- webdriver.findElement(By.id("input"))
          _      <- search.sendKeysM("test")
          _      <- search.clearM
        } yield assert(search.getAttribute("value"))(isEmptyString)

        effect.provideCustomLayer(testLayer())
      },
      test("Element such as button should be clickable") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.tagName("a"))
          _      <- button.clickM
          url    <- webdriver.url
        } yield assert(url)(equalTo("https://github.com/"))

        effect.provideCustomLayer(testLayer())
      },
      test("Element such as form should be submitable") {
        val effect = for {
          _    <- webdriver.link(testWebsite)
          form <- webdriver.findElement(By.tagName("form"))
          _    <- form.submitM
          url  <- webdriver.url
        } yield assert(url)(equalTo("https://github.com/"))

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteGetterElement: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Getter Element Spec")(
      test("Element has attributes") {
        val effect = for {
          _     <- webdriver.link(testWebsite)
          h1    <- webdriver.findElement(By.id("test"))
          title <- h1.getAttributeM("title")
        } yield assert(title)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      test("Element can have a text") {
        val effect = for {
          _    <- webdriver.link(testWebsite)
          h1   <- webdriver.findElement(By.id("test"))
          text <- h1.getTextM
        } yield assert(text)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      test("Element has css values") {
        val effect = for {
          _     <- webdriver.link(testWebsite)
          h1    <- webdriver.findElement(By.id("test"))
          value <- h1.getCssValueM("color")
        } yield assert(value)(equalTo("rgba(0, 0, 255, 0.5)"))

        effect.provideCustomLayer(testLayer(true, false))
      },
      test("Element has a tag name") {
        val effect = for {
          _   <- webdriver.link(testWebsite)
          h1  <- webdriver.findElement(By.tagName("h1"))
          tag <- h1.getTagNameM
        } yield assert(tag)(equalTo("h1"))

        effect.provideCustomLayer(testLayer())
      },
      test("Element has a location, as size and a rect") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            h1       <- webdriver.findElement(By.tagName("h1"))
            location <- h1.getLocationM
            size     <- h1.getSizeM
            rect     <- h1.getRectM
          } yield {
            assert(location.getX)(equalTo(rect.getX)) &&
            assert(location.getY)(equalTo(rect.getY)) &&
            assert(size.getWidth)(equalTo(rect.getWidth)) &&
            assert(size.getHeight)(equalTo(rect.getHeight))
          }

        effect.provideCustomLayer(testLayer())
      },
      test("Element is show by default") {
        val effect =
          for {
            _         <- webdriver.link(testWebsite)
            element   <- webdriver.findElement(By.id("show"))
            displayed <- element.isDisplayedM
          } yield assert(displayed)(isTrue)

        effect.provideCustomLayer(testLayer(true, true))
      },
      test("Element can be hidden") {
        val effect =
          for {
            _         <- webdriver.link(testWebsite)
            element   <- webdriver.findElement(By.id("hide"))
            displayed <- element.isDisplayedM
          } yield assert(displayed)(isFalse)

        effect.provideCustomLayer(testLayer(true, true))
      },
      test("Element is enable by default") {
        val effect =
          for {
            _       <- webdriver.link(testWebsite)
            element <- webdriver.findElement(By.id("enable"))
            enabled <- element.isEnabledM
          } yield assert(enabled)(isTrue)

        effect.provideCustomLayer(testLayer())
      },
      test("Element can be disable") {
        val effect =
          for {
            _       <- webdriver.link(testWebsite)
            element <- webdriver.findElement(By.id("disable"))
            enabled <- element.isEnabledM
          } yield assert(enabled)(isFalse)

        effect.provideCustomLayer(testLayer())
      },
      test("Element is unselected by default") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            element  <- webdriver.findElement(By.id("unselected"))
            selected <- element.isSelectedM
          } yield assert(selected)(isFalse)

        effect.provideCustomLayer(testLayer())
      },
      test("Element can be selected") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            element  <- webdriver.findElement(By.id("selected"))
            selected <- element.isSelectedM
          } yield assert(selected)(isTrue)

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteElements: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Find Elements Spec")(
      test("Element can find an element") {
        val effect = for {
          _    <- webdriver.link(testWebsite)
          div  <- webdriver.findElement(By.id("div"))
          p    <- div.findElementM(By.tagName("p"))
          text <- p.getTextM
        } yield assert(text)(equalTo("Test 3"))

        effect.provideCustomLayer(testLayer())
      },
      test("Element can find several elements") {
        val effect = for {
          _     <- webdriver.link(testWebsite)
          div   <- webdriver.findElement(By.id("div"))
          ps    <- div.findElementsM(By.tagName("p"))
          texts <- ZIO.succeed(ps.map(_.getText()))
        } yield assert(texts)(equalTo(List("Test 3")))

        effect.provideCustomLayer(testLayer())
      },
      test("Element can check if an element exist") {
        val effect = for {
          _    <- webdriver.link(testWebsite)
          div  <- webdriver.findElement(By.id("div"))
          bool <- div.hasElementM(By.tagName("p"))
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer())
      }
    )

  def spec: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Element Spec")(suiteInteractElement, suiteGetterElement, suiteElements)
}
