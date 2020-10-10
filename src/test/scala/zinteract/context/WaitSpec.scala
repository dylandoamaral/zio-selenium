package zinteract.test

import zio.{Schedule, ZIO}
import zio.test._
import zio.test.Assertion._
import zio.test.environment._
import zio.duration.durationInt

import zinteract.test.TestDriver.testLayer
import zinteract.test.SessionSpec.fakeValue
import zinteract.element._
import zinteract.context._
import zinteract.session

import org.openqa.selenium.{By, NoSuchElementException, TimeoutException}

import scala.io.Source

object WaitSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/WaitSpec.html").getPath
  val testWebsite = s"file://$testPath"

  def spec =
    suite("Wait Spec")(
      testM("Find element can not wait") {
        val effect = for {
          _       <- session.link(testWebsite)
          element <- session.findElement(By.tagName("p"))(None)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(false, true)).run)(
          fails(isSubtype[NoSuchElementException](anything))
        )
      },
      testM("Find element can use fluent wait") {
        val effect =
          for {
            _       <- session.link(testWebsite)
            waiter  <- session.getFluentWaiter(200.milliseconds, 1.second)
            element <- session.findElement(By.tagName("p"))(waiter)
            text    <- element.getTextM
          } yield assert(text)(equalTo("Test"))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find element can use scheduled wait") {
        val waiter = Scheduled(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _       <- session.link(testWebsite)
            element <- session.findElement(By.tagName("p"))(waiter)
            text    <- element.getTextM
          } yield assert(text)(equalTo("Test"))

        Live.live(effect.provideCustomLayer(testLayer(false, true)))
      },
      testM("Find elements can not wait") {
        val effect =
          for {
            _        <- session.link(testWebsite)
            elements <- session.findElements(By.tagName("p"))
          } yield assert(elements)(isEmpty)

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find elements can use fluent wait") {
        val effect =
          for {
            _        <- session.link(testWebsite)
            waiter   <- session.getFluentWaiter(200.milliseconds, 1.second)
            elements <- session.findElements(By.tagName("p"))(waiter)
            texts    <- ZIO.succeed(elements.map(_.getText()))
          } yield assert(texts)(equalTo(List("Test")))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Find elements can use scheduled wait") {
        val waiter = Scheduled(Schedule.recurs(5) && Schedule.spaced(200.milliseconds))
        val effect =
          for {
            _        <- session.link(testWebsite)
            elements <- session.findElements(By.tagName("p"))(waiter)
            texts    <- ZIO.succeed(elements.map(_.getText()))
          } yield assert(texts)(equalTo(List("Test")))

        Live.live(effect.provideCustomLayer(testLayer(false, true)))
      }
    )
}
