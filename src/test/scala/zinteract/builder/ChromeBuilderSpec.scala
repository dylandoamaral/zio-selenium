package zinteract.test

import zio.test.Assertion._
import zio.test._

import zinteract.builder.ChromeBlueprint.ChromeBlueprint
import zinteract.builder.{ChromeBlueprint, CommonBlueprint, chrome}

import org.openqa.selenium.PageLoadStrategy

import scala.jdk.CollectionConverters._

object ChromeBuilderSpec extends DefaultRunnableSpec {
  def assertCapability(blueprint: ChromeBlueprint)(key: String, value: String) =
    for {
      capabilities <- (chrome using blueprint).buildOptions.map(_.asMap.asScala)
    } yield assert(capabilities.get(key).map(_.toString))(isSome(equalTo(value)))

  def assertArgument(blueprint: ChromeBlueprint)(argument: String) =
    BuilderUtils.assertArgument(chrome, blueprint)(argument)

  def suiteChromeBuilder =
    suite("Chrome Builder Spec")(
      test("We can build an unit chrome builder") {
        val builder = chrome
        assert(builder.path)(isNone)
      },
      test("We can update the path of a build with 'at'") {
        val builder = chrome at "path"
        assert(builder.path)(isSome(equalTo("path")))
      },
      test("We can update the path of a build with '>'") {
        val builder = chrome > "path"
        assert(builder.path)(isSome(equalTo("path")))
      },
      testM("We can update the blueprint of a build with 'using'") {
        val builder = chrome using ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      },
      testM("We can update the blueprint of a build with '>>'") {
        val builder = chrome >> ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      }
    )

  def suiteChromeBlueprint =
    suite("Chrome Default Blueprint Spec")(
      testM("Chrome Blueprint can overload default blueprint") {
        val blueprint = ChromeBlueprint.default &&
          ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(blueprint)("pageLoadStrategy", "eager")
      },
      testM("Chrome Blueprint can compose two action") {
        val blueprint = ChromeBlueprint.noGpu and ChromeBlueprint.noExtensions
        assertArgument(blueprint)("--disable-gpu")
        assertArgument(blueprint)("--disable-extensions")
      },
      testM("Chrome Blueprint can set capability") {
        val blueprint = CommonBlueprint.setCapability("key", "value") && ChromeBlueprint.default
        assertCapability(blueprint)("key", "value")
      },
      testM("Chrome Blueprint can set another pageLoadStrategy") {
        assertCapability(ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER))("pageLoadStrategy", "eager")
      },
      testM("Chrome Blueprint can add an argument") {
        assertArgument(ChromeBlueprint.addArgument("--argument"))("--argument")
      },
      testM("Chrome Blueprint can add arguments") {
        val blueprint = ChromeBlueprint.addArguments(List("--argument", "--argument2"))
        assertArgument(blueprint)("--argument")
        assertArgument(blueprint)("--argument2")
      },
      testM("Chrome Blueprint can disable gpu") {
        assertArgument(ChromeBlueprint.noGpu)("--disable-gpu")
      },
      testM("Chrome Blueprint can disable extensions") {
        assertArgument(ChromeBlueprint.noExtensions)("--disable-extensions")
      },
      testM("Chrome Blueprint can disable popup blocking") {
        assertArgument(ChromeBlueprint.noPopupBlocking)("--disable-popup-blocking")
      },
      testM("Chrome Blueprint can start fullscreen") {
        assertArgument(ChromeBlueprint.fullscreen)("--start-fullscreen")
      },
      testM("Chrome Blueprint can be headless") {
        assertArgument(ChromeBlueprint.headless)("--headless")
        assertArgument(ChromeBlueprint.headless)("--disable-gpu")
      }
    )

  def spec = suite("Chrome Blueprint Spec")(suiteChromeBlueprint)
}
