package zinteract.test

import zio.duration.durationInt
import zio.test.Assertion._
import zio.test._
import zio.test.environment._
import zio.{Schedule, ZIO}

import zinteract.context._
import zinteract.element._
import zinteract.test.TestDriver.testLayer
import zinteract.webdriver

import org.openqa.selenium.By

object WaitSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/WaitSpec.html").getPath
  val testWebsite = s"file://$testPath"

  def spec =
    suite("Wait Spec")(
      testM("Find element can not wait") {
        val effect = for {
          _       <- webdriver.link(testWebsite)
          element <- webdriver.findElement(By.tagName("p"))(None)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(false, true)).run)(
          fails(isSubtype[Throwable](anything))
        )
      },
      testM("Find element can use fluent wait") {
        val effect =
          for {
            _       <- webdriver.link(testWebsite)
            waiter  <- webdriver.defineFluentWaiter(200.milliseconds, 1.second)
            element <- webdriver.findElement(By.tagName("p"))(waiter)
            text    <- element.getTextM
          } yield assert(text)(equalTo("Test"))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find element can use scheduled wait") {
        val waiter = Scheduled(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _       <- webdriver.link(testWebsite)
            element <- webdriver.findElement(By.tagName("p"))(waiter)
            text    <- element.getTextM
          } yield assert(text)(equalTo("Test"))

        Live.live(effect.provideCustomLayer(testLayer(false, true)))
      },
      testM("Find elements can not wait") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            elements <- webdriver.findElements(By.tagName("p"))
          } yield assert(elements)(isEmpty)

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find elements can use fluent wait") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            waiter   <- webdriver.defineFluentWaiter(200.milliseconds, 1.second)
            elements <- webdriver.findElements(By.tagName("p"))(waiter)
            texts    <- ZIO.succeed(elements.map(_.getText()))
          } yield assert(texts)(equalTo(List("Test")))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find elements can use fluent wait and returns empty list if no element") {
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            waiter   <- webdriver.defineFluentWaiter(200.milliseconds, 1.second)
            elements <- webdriver.findElements(By.id("notexist"))(waiter)
          } yield assert(elements)(isEmpty)

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find elements can use scheduled wait") {
        val waiter = Scheduled(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            elements <- webdriver.findElements(By.tagName("p"))(waiter)
            texts    <- ZIO.succeed(elements.map(_.getText()))
          } yield assert(texts)(equalTo(List("Test")))

        Live.live(effect.provideCustomLayer(testLayer(false, true)))
      },
      testM("Find elements can use scheduled wait and returns empty list if no element") {
        val waiter = Scheduled(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _        <- webdriver.link(testWebsite)
            elements <- webdriver.findElements(By.id("notexist"))(waiter)
          } yield assert(elements)(isEmpty)

        Live.live(effect.provideCustomLayer(testLayer(false, true)))
      }
    )
}
