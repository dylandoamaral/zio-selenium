package zinteract.test

import zio.test._
import zio.test.Assertion._

import zinteract.webdriver.ChromeBuilder

import scala.jdk.CollectionConverters._

object BlueprintSpec extends DefaultRunnableSpec {
  def suiteChromeBlueprint =
    suite("Chrome Blueprint Spec")(
      testM("Chrome Blueprint can set pageLoadStrategy") {
        val builder = ChromeBuilder("")
        val effect =
          for {
            options <- builder.buildOptions.map(_.asMap.asScala)
          } yield assert(options.get("pageLoadStrategy").map(_.toString))(isSome(equalTo("normal")))

        effect
      }
    )

  def spec = suite("Blueprint Spec")(suiteChromeBlueprint)
}
