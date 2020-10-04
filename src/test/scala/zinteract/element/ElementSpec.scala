package zinteract

import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.element._
import zinteract.SurferSpec.{fakeValue, testLayer, testWebsite}

object ElementSpec extends DefaultRunnableSpec {
  def suiteInteractElement =
    suite("Interact Element Spec")(
      testM("Element such as input should be writable") {
        val effect = for {
          _      <- surfer.link(testWebsite)
          search <- surfer.findElementByClass("search_query")
          _      <- search.sendKeysM("test")
        } yield assert(search.getAttribute("value"))(equalTo("test"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Element such as input should be cleanable") {
        val effect = for {
          _      <- surfer.link(testWebsite)
          search <- surfer.findElementByClass("search_query")
          _      <- search.sendKeysM("test")
          _      <- search.clearM
        } yield assert(search.getAttribute("value"))(isEmptyString)

        effect.provideCustomLayer(testLayer)
      },
      testM("Element such as button should be clickable") {
        val effect = for {
          _      <- surfer.link(testWebsite)
          button <- surfer.findElementByLinkText("Women")
          _      <- button.clickM
          url    <- surfer.url
        } yield assert(url)(equalTo(s"$testWebsite?id_category=3&controller=category"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Element form should be submitable") {
        val effect = for {
          _             <- surfer.link(testWebsite)
          newsletter    <- surfer.findElementById("newsletter_block_left")
          form          <- newsletter.findElementByTagNameM("form")
          input         <- form.findElementByTagNameM("input")
          _             <- input.sendKeysM("test@test.com")
          _             <- form.submitM
          inputReloaded <- surfer.findElementByClass("inputNew")
          value         <- inputReloaded.getAttributeM("value")
        } yield assert(value)(equalTo("Invalid email address."))

        effect.provideCustomLayer(testLayer)
      }
    )

  def suiteGetterElement =
    suite("Getter Element Spec")(
      testM("Element has attributes") {
        val effect = for {
          _      <- surfer.link(testWebsite)
          button <- surfer.findElementByLinkText("Women")
          title  <- button.getAttributeM("title")
        } yield assert(title)(equalTo("Women"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Element can have a text") {
        val effect = for {
          _      <- surfer.link(testWebsite)
          button <- surfer.findElementByLinkText("Women")
          text   <- button.getTextM
        } yield assert(text)(equalTo("Women"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Element can have css values") {
        val effect = for {
          _      <- surfer.link(testWebsite)
          button <- surfer.findElementByLinkText("Women")
          value  <- button.getCssValueM("color")
        } yield assert(value)(equalTo("rgba(0, 0, 0, 1)")) // CSS is disabled

        effect.provideCustomLayer(testLayer)
      },
      testM("Element has a tag name") {
        val effect = for {
          _   <- surfer.link(testWebsite)
          img <- surfer.findElementByTagName("img")
          tag <- img.getTagNameM
        } yield assert(tag)(equalTo("img"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Element has a location, as size and a rect") {
        val effect =
          for {
            _        <- surfer.link(testWebsite)
            button   <- surfer.findElementByLinkText("Women")
            location <- button.getLocationM
            size     <- button.getSizeM
            rect     <- button.getRectM
          } yield {
            assert(location.getX)(equalTo(rect.getX)) &&
            assert(location.getY)(equalTo(rect.getY)) &&
            assert(size.getWidth)(equalTo(rect.getWidth)) &&
            assert(size.getHeight)(equalTo(rect.getHeight))
          }

        effect.provideCustomLayer(testLayer)
      }
    )

  def spec = suite("Element Spec")(suiteInteractElement, suiteGetterElement)
}
