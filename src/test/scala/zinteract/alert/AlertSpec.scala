package zinteract.alert

import zio._
import zio.test.Assertion._
import zio.test._
import zinteract.element._
import zinteract.webdriver
import org.openqa.selenium.{By, TimeoutException}
import zinteract.TestDriver.testLayer

object AlertSpec extends DefaultRunnableSpec {
  val testPath: String = getClass.getResource("/AlertSpec.html").getPath
  val testWebsite      = s"file://$testPath"

  def suiteAlerts: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Alerts Spec")(
      test("Alert has a text") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("alert"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          text   <- alert.getTextM
        } yield assert(text)(equalTo("Test alert"))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      }
    )

  def suiteConfirms: Spec[ZEnv, TestFailure[Nothing], TestSuccess] =
    suite("Confirms Spec")(
      test("Confirm can be dismiss") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("confirm"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.dismissM
          _      <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(jsEnabled = true)).exit)(
          fails(isSubtype[TimeoutException](anything))
        )
      }
    )

  def suitePrompts: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Prompts Spec")(
      test("Prompt can be dismiss") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("prompt"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.dismissM
          _      <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(jsEnabled = true)).exit)(
          fails(isSubtype[TimeoutException](anything))
        )
      },
      test("Prompt can be accept") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("prompt"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.acceptM
          _      <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
        } yield ()

        assertM(effect.provideCustomLayer(testLayer(jsEnabled = true)).exit)(
          fails(isSubtype[TimeoutException](anything))
        )
      },
      test("Prompt has a text input") {
        val effect = for {
          _      <- webdriver.link(testWebsite)
          button <- webdriver.findElement(By.id("prompt"))
          _      <- button.clickM
          alert  <- webdriver.getAlert(100.milliseconds, 500.milliseconds)
          _      <- alert.sendKeysM("test")
        } yield assertCompletes

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      }
    )

  def spec: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Alert Spec")(suiteAlerts, suiteConfirms, suitePrompts)
}
