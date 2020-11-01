package zinteract.test

import zio.ZIO
import zio.test._
import zio.test.Assertion._

import zinteract.webdriver.{ChromeBlueprintOps, ChromeBuilder}
import zinteract.webdriver.ChromeBlueprintOps.ChromeBlueprint

import scala.jdk.CollectionConverters._
import org.openqa.selenium.PageLoadStrategy

object ChromeBlueprintSpec extends DefaultRunnableSpec {
  def assertCapability(blueprint: ChromeBlueprint)(key: String, value: String) =
    for {
      capabilities <- ChromeBuilder("", blueprint).buildOptions.map(_.asMap.asScala)
    } yield assert(capabilities.get(key).map(_.toString))(isSome(equalTo(value)))

  def assertArgument(blueprint: ChromeBlueprint)(argument: String) =
    for {
      options <- ChromeBuilder("", blueprint).buildOptions
      arguments <- ZIO.succeed({
        val field = options.getClass.getDeclaredField("args")
        field.setAccessible(true)
        field
          .get(options)
          .asInstanceOf[java.util.List[String]]
          .asScala
          .toList
      })
    } yield assert(arguments)(contains(argument))

  def suiteChromeDefaultBlueprint =
    suite("Chrome Default Blueprint Spec")(
      testM("Default Chrome Blueprint has a normal pageLoadStrategy") {
        assertCapability(ChromeBlueprintOps.default)("pageLoadStrategy", "normal")
      }
    )

  def suiteChromeBlueprint =
    suite("Chrome Default Blueprint Spec")(
      testM("Chrome Blueprint can overload default blueprint") {
        val blueprint = ChromeBlueprintOps.default <>
          ChromeBlueprintOps.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(blueprint)("pageLoadStrategy", "eager")
      },
      testM("Chrome Blueprint can set another pageLoadStrategy") {
        assertCapability(ChromeBlueprintOps.setLoadPageStrategy(PageLoadStrategy.EAGER))("pageLoadStrategy", "eager")
      },
      testM("Chrome Blueprint can add an argument") {
        assertArgument(ChromeBlueprintOps.addArgument("--argument"))("--argument")
      },
      testM("Chrome Blueprint can disable gpu") {
        assertArgument(ChromeBlueprintOps.noGpu)("--disable-gpu")
      },
      testM("Chrome Blueprint can disable extensions") {
        assertArgument(ChromeBlueprintOps.noExtensions)("--disable-extensions")
      },
      testM("Chrome Blueprint can disable popup blocking") {
        assertArgument(ChromeBlueprintOps.noPopupBlocking)("--disable-popup-blocking")
      },
      testM("Chrome Blueprint can start fullscreen") {
        assertArgument(ChromeBlueprintOps.fullscreen)("--start-fullscreen")
      },
      testM("Chrome Blueprint can be headless") {
        assertArgument(ChromeBlueprintOps.headless)("--headless")
        assertArgument(ChromeBlueprintOps.headless)("--disable-gpu")
      }
    )

  def spec = suite("Chrome Blueprint Spec")(suiteChromeDefaultBlueprint, suiteChromeBlueprint)
}
