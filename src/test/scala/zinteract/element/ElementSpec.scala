package zinteract

import zio.ZIO
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.TestDriver.testLayer
import zinteract.SessionSpec.fakeValue
import zinteract.element._

import org.openqa.selenium.By

object ElementSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/ElementSpec.html").getPath
  val testWebsite = s"file://$testPath"

  def suiteInteractElement =
    suite("Interact Element Spec")(
      testM("Element such as input should be writable") {
        val effect = for {
          _      <- session.link(testWebsite)
          search <- session.findElement(By.id("input"))
          _      <- search.sendKeysM("test")
        } yield assert(search.getAttribute("value"))(equalTo("test"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element such as input should be cleanable") {
        val effect = for {
          _      <- session.link(testWebsite)
          search <- session.findElement(By.id("input"))
          _      <- search.sendKeysM("test")
          _      <- search.clearM
        } yield assert(search.getAttribute("value"))(isEmptyString)

        effect.provideCustomLayer(testLayer())
      },
      testM("Element such as button should be clickable") {
        val effect = for {
          _      <- session.link(testWebsite)
          button <- session.findElement(By.tagName("a"))
          _      <- button.clickM
          url    <- session.url
        } yield assert(url)(equalTo("https://www.google.com/"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element such as form should be submitable") {
        val effect = for {
          _    <- session.link(testWebsite)
          form <- session.findElement(By.tagName("form"))
          _    <- form.submitM
          url  <- session.url
        } yield assert(url)(equalTo("https://www.google.com/"))

        effect.provideCustomLayer(testLayer())
      }
    )

  def suiteGetterElement =
    suite("Getter Element Spec")(
      testM("Element has attributes") {
        val effect = for {
          _     <- session.link(testWebsite)
          h1    <- session.findElement(By.id("test"))
          title <- h1.getAttributeM("title")
        } yield assert(title)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element can have a text") {
        val effect = for {
          _    <- session.link(testWebsite)
          h1   <- session.findElement(By.id("test"))
          text <- h1.getTextM
        } yield assert(text)(equalTo("Test 1"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element has css values") {
        val effect = for {
          _     <- session.link(testWebsite)
          h1    <- session.findElement(By.id("test"))
          value <- h1.getCssValueM("color")
        } yield assert(value)(equalTo("rgba(0, 0, 255, 0.5)"))

        effect.provideCustomLayer(testLayer(true, false))
      },
      testM("Element has a tag name") {
        val effect = for {
          _   <- session.link(testWebsite)
          h1  <- session.findElement(By.tagName("h1"))
          tag <- h1.getTagNameM
        } yield assert(tag)(equalTo("h1"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element has a location, as size and a rect") {
        val effect =
          for {
            _        <- session.link(testWebsite)
            h1       <- session.findElement(By.tagName("h1"))
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
      }
    )

  def suiteElements =
    suite("Find Elements Spec")(
      testM("Element can find an element") {
        val effect = for {
          result <- session.link(testWebsite)
          div    <- session.findElement(By.id("div"))
          p      <- div.findElementM(By.tagName("p"))
          text   <- p.getTextM
        } yield assert(text)(equalTo("Test 3"))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element can find several elements") {
        val effect = for {
          result <- session.link(testWebsite)
          div    <- session.findElement(By.id("div"))
          ps     <- div.findElementsM(By.tagName("p"))
          texts  <- ZIO.succeed(ps.map(_.getText()))
        } yield assert(texts)(equalTo(List("Test 3")))

        effect.provideCustomLayer(testLayer())
      },
      testM("Element can check if an element exist") {
        val effect = for {
          result <- session.link(testWebsite)
          div    <- session.findElement(By.id("div"))
          bool   <- div.hasElementM(By.tagName("p"))
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer())
      }
    )

  def spec = suite("Element Spec")(suiteInteractElement, suiteGetterElement, suiteElements)
}
