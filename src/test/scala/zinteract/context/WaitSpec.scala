package zinteract.context

import org.openqa.selenium.By
import zinteract.TestDriver.testLayer
import zinteract.element._
import zinteract.webdriver
import zio.test.Assertion._
import zio.test._
import zio._

object WaitSpec extends DefaultRunnableSpec {
  val testPath: String = getClass.getResource("/WaitSpec.html").getPath
  val testWebsite      = s"file://$testPath"

  def spec: Spec[ZEnv with Live, TestFailure[Throwable], TestSuccess] =
    suite("Wait Spec")(
      test("Find element can not wait") {
        val effect = for {
          _ <- webdriver.link(testWebsite)
          _ <- webdriver.findElement(By.tagName("p"))(DontWait)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(jsEnabled = true)).exit)(
          fails(isSubtype[Throwable](anything))
        )
      },
      test("Find element can use fluent wait") {
        val effect =
          for {
            _       <- webdriver.link(testWebsite)
            waiter  <- webdriver.defineFluentWaiter(200.milliseconds, 1.second)
            element <- webdriver.findElement(By.tagName("p"))(waiter)
            text    <- element.getTextM
          } yield assert(text)(equalTo("Test"))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("Find element can use scheduled wait") {
        val waiter = WaitUsingZIO(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _       <- webdriver.link(testWebsite)
            element <- webdriver.findElement(By.tagName("p"))(waiter)
            text    <- element.getTextM
          } yield assert(text)(equalTo("Test"))

        Live.live(effect.provideCustomLayer(testLayer(jsEnabled = true)))
      },
      test("Find elements can not wait") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            elements <- webdriver.findElements(By.tagName("p"))
          } yield assert(elements)(isEmpty)

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("Find elements can use fluent wait") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            waiter   <- webdriver.defineFluentWaiter(200.milliseconds, 1.second)
            elements <- webdriver.findElements(By.tagName("p"))(waiter)
            texts    <- ZIO.succeed(elements.map(_.getText()))
          } yield assert(texts)(equalTo(List("Test")))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("Find elements can use fluent wait and returns empty list if no element") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            waiter   <- webdriver.defineFluentWaiter(200.milliseconds, 1.second)
            elements <- webdriver.findElements(By.id("notexist"))(waiter)
          } yield assert(elements)(isEmpty)

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("Find elements can use scheduled wait") {
        val waiter = WaitUsingZIO(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            elements <- webdriver.findElements(By.tagName("p"))(waiter)
            texts    <- ZIO.succeed(elements.map(_.getText()))
          } yield assert(texts)(equalTo(List("Test")))

        Live.live(effect.provideCustomLayer(testLayer(jsEnabled = true)))
      },
      test("Find elements can use scheduled wait and returns empty list if no element") {
        val waiter = WaitUsingZIO(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            elements <- webdriver.findElements(By.id("notexist"))(waiter)
          } yield assert(elements)(isEmpty)

        Live.live(effect.provideCustomLayer(testLayer(jsEnabled = true)))
      }
    )
}
