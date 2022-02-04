package zinteract.builder

import zio.test.Assertion._
import zio.test._
import zinteract.webdriver
import zio.ZEnv

object BuilderSpec extends DefaultRunnableSpec {
  def suiteWebdriverLayer: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Webdriver Layer Spec")(
      test("We can use chromedriver") {
        val blueprint = ChromeBlueprint.headless
        val builder   = chrome at s"${System.getenv("HOME")}/Webdriver/chromedriver" using blueprint

        val effect = webdriver.link("https://www.google.com/") *> webdriver.url

        assertM(effect.provideCustomLayer(builder.buildLayer))(equalTo("https://www.google.com/"))
      },
      test("We can use geckodriver") {
        val blueprint = FirefoxBlueprint.headless
        val builder   = firefox at s"${System.getenv("HOME")}/Webdriver/geckodriver" using blueprint

        val effect = webdriver.link("https://www.google.com/") *> webdriver.url

        assertM(effect.provideCustomLayer(builder.buildLayer))(equalTo("https://www.google.com/"))
      }
    )

  def spec: Spec[ZEnv, TestFailure[Throwable], TestSuccess] = suite("Webdriver Spec")(suiteWebdriverLayer)
}
