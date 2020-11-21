package zinteract.test

import zio.duration.durationInt
import zio.test.Assertion._
import zio.test._

import zinteract.alert._
import zinteract.element._
import zinteract.test.TestDriver.testLayer
import zinteract.webdriver

import org.openqa.selenium.{By, TimeoutException}

object AlertSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/AlertSpec.html").getPath
  val testWebsite = s"file://$testPath"

  def suiteAlerts =
    suite("Alerts Spec")(
      testM("Alert has a text") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("alert"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          text   <- alert.getTextM
        } yield assert(text)(equalTo("Test alert"))

        effect.provideCustomLayer(testLayer(false, true))
      }
    )

  def suiteConfirms =
    suite("Confirms Spec")(
      testM("Confirm can be dismiss") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("confirm"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.dismissM
          _      <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(false, true)).run)(
          fails(isSubtype[TimeoutException](anything))
        )
      }
    )

  def suitePrompts =
    suite("Prompts Spec")(
      testM("Prompt can be dismiss") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("prompt"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.dismissM
          _      <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(false, true)).run)(
          fails(isSubtype[TimeoutException](anything))
        )
      },
      testM("Prompt can be accept") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("prompt"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.acceptM
          _      <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(false, true)).run)(
          fails(isSubtype[TimeoutException](anything))
        )
      },
      testM("Prompt has a text input") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("prompt"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.sendKeysM("test")
        } yield assertCompletes

        effect.provideCustomLayer(testLayer(false, true))
      }
    )

  def spec = suite("Alert Spec")(suiteAlerts, suiteConfirms, suitePrompts)
}
