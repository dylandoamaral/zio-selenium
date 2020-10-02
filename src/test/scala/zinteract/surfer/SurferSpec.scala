package zinteract

import zio.ZIO
import zio.test._
import zio.test.Assertion._
import zio.test.environment._

import zinteract.webdriver.WebDriver
import zinteract.surfer.Surfer

import org.openqa.selenium.By

object SurferSpec extends DefaultRunnableSpec {
  val testLayer   = TestDriver.testDriver >>> Surfer.Service.live
  val testWebsite = "http://automationpractice.com/index.php"
  val fakeValue   = "9b233f60-01c2-11eb-adc1-0242ac120002"

  def suiteUrl =
    suite("Url Spec")(
      testM("Surfer should link to about:blank by default") {
        val effect = for {
          url <- surfer.url
        } yield assert(url)(equalTo("about:blank"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should link correctly to a correct url") {
        val effect = for {
          _   <- surfer.link(testWebsite)
          url <- surfer.url
        } yield assert(url)(equalTo(testWebsite))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer shouldn't link to an incorrect url") {
        val effect = for {
          result <- surfer.link(fakeValue)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer).run)(
          fails(isSubtype[org.openqa.selenium.WebDriverException](anything))
        )
      },
      testM("Surfer should link correctly to a correct domain") {
        val effect = for {
          _   <- surfer.link(testWebsite)
          url <- surfer.domain
        } yield assert(url)(equalTo("automationpractice.com"))

        effect.provideCustomLayer(testLayer)
      }
    )

  def suiteFindElement =
    suite("Find Element Spec")(
      testM("Surfer should find an element using By") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElement(new By.ById("editorial_image_legend"))
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Subsidiary of seleniumframework.com"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Id") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementById("editorial_image_legend")
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Subsidiary of seleniumframework.com"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Class") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByClass("homefeatured")
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Popular"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Name") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByName("description")
          content <- ZIO.succeed(element.getAttribute("content"))
        } yield assert(content)(equalTo("Shop powered by PrestaShop"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Tag name") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByTagName("div")
          id      <- ZIO.succeed(element.getAttribute("id"))
        } yield assert(id)(equalTo("page"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Xpath") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByXPath("/html/body/div/div[1]/header/div[3]/div/div/div[6]/ul/li[1]/a")
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Women"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by CSS selector") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByCssSelector("#block_top_menu > ul > li:nth-child(1) > a")
          text    <- ZIO.succeed(element.getText())
        } yield assert(text)(equalTo("Women"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Link text") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByLinkText("Contact us")
          href    <- ZIO.succeed(element.getAttribute("href"))
        } yield assert(href)(equalTo(s"$testWebsite?controller=contact"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Partial link text") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementByPartialLinkText("Contact")
          href    <- ZIO.succeed(element.getAttribute("href"))
        } yield assert(href)(equalTo(s"$testWebsite?controller=contact"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer shouldn't find an unknown element using findElement") {
        val effect = for {
          result <- surfer.link(testWebsite)
          _      <- surfer.findElementById(fakeValue)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer).run)(
          fails(isSubtype[org.openqa.selenium.NoSuchElementException](anything))
        )
      }
    )

  def suiteFindElements =
    suite("Find Elements Spec")(
      testM("Surfer should find elements using By") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElements(new By.ById("editorial_image_legend"))
          text    <- ZIO.succeed(element.map(_.getText()))
        } yield assert(text(0))(equalTo("Subsidiary of seleniumframework.com"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Id") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsById("editorial_image_legend")
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts(0))(equalTo("Subsidiary of seleniumframework.com"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Class") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsByClass("homefeatured")
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts(0))(equalTo("Popular"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Name") {
        val effect = for {
          result   <- surfer.link(testWebsite)
          element  <- surfer.findElementsByName("description")
          contents <- ZIO.succeed(element.map(_.getAttribute("content")))
        } yield assert(contents(0))(equalTo("Shop powered by PrestaShop"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Tag name") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsByTagName("div")
          ids     <- ZIO.succeed(element.map(_.getAttribute("id")))
        } yield assert(ids(0))(equalTo("page"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Xpath") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsByXPath("/html/body/div/div[1]/header/div[3]/div/div/div[6]/ul/li[1]/a")
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts(0))(equalTo("Women"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by CSS selector") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsByCssSelector("#block_top_menu > ul > li:nth-child(1) > a")
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts(0))(equalTo("Women"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Link text") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsByLinkText("Contact us")
          hrefs   <- ZIO.succeed(element.map(_.getAttribute("href")))
        } yield assert(hrefs(0))(equalTo(s"$testWebsite?controller=contact"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find elements by Partial link text") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsByPartialLinkText("Contact")
          hrefs   <- ZIO.succeed(element.map(_.getAttribute("href")))
        } yield assert(hrefs(0))(equalTo(s"$testWebsite?controller=contact"))

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer shouldn't find an unknown element using findElements") {
        val effect = for {
          result  <- surfer.link(testWebsite)
          element <- surfer.findElementsById(fakeValue)
          texts   <- ZIO.succeed(element.map(_.getText()))
        } yield assert(texts)(isEmpty)

        effect.provideCustomLayer(testLayer)
      }
    )

  def suiteHasElement =
    suite("Has Element Spec")(
      testM("Surfer should find an element using By") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElement(new By.ById("editorial_image_legend"))
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Id") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithId("editorial_image_legend")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Class") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithClass("homefeatured")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Name") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithName("description")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Tag name") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithTagName("div")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Xpath") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithXPath("/html/body/div/div[1]/header/div[3]/div/div/div[6]/ul/li[1]/a")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by CSS selector") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithCssSelector("#block_top_menu > ul > li:nth-child(1) > a")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Link text") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithLinkText("Contact us")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer should find an element by Partial link text") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElementWithPartialLinkText("Contact")
        } yield assert(bool)(isTrue)

        effect.provideCustomLayer(testLayer)
      },
      testM("Surfer shouldn't find an unknown element using hasElement") {
        val effect = for {
          result <- surfer.link(testWebsite)
          bool   <- surfer.hasElement(new By.ById(fakeValue))
        } yield assert(bool)(isFalse)

        effect.provideCustomLayer(testLayer)
      }
    )

  def spec = suite("Surfer Spec")(suiteUrl, suiteFindElement, suiteFindElements, suiteHasElement)
}
