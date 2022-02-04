package zinteract.builder

import zio.test.Assertion._
import zio.test._
import zinteract.builder.ChromeBlueprint.ChromeBlueprint
import org.openqa.selenium.PageLoadStrategy
import zio.ZIO

import java.util
import scala.jdk.CollectionConverters._

object ChromeBuilderSpec extends DefaultRunnableSpec {
  def assertCapability(blueprint: ChromeBlueprint)(key: String, value: String): ZIO[Any, Throwable, TestResult] =
    for {
      capabilities <- (chrome using blueprint).buildOptions.map(_.asMap.asScala)
    } yield assert(capabilities.get(key).map(_.toString))(isSome(equalTo(value)))

  def assertArgument(blueprint: ChromeBlueprint)(argument: String): ZIO[Any, Throwable, TestResult] =
    for {
      options <- (chrome using blueprint).buildOptions
      arguments <- ZIO.succeed({
        val field = options.getClass.getSuperclass.getDeclaredField("args")
        field.setAccessible(true)
        field
          .get(options)
          .asInstanceOf[util.List[String]]
          .asScala
          .toList
      })
    } yield assert(arguments)(contains(argument))

  def suiteChromeBuilder: Spec[Any, TestFailure[Throwable], TestSuccess] =
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
      test("We can update the blueprint of a build with 'using'") {
        val builder = chrome using ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      },
      test("We can update the blueprint of a build with '>>'") {
        val builder = chrome >> ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      }
    )

  def suiteChromeBlueprint: Spec[Any, TestFailure[Any], TestSuccess] =
    suite("Chrome Default Blueprint Spec")(
      test("Chrome Blueprint can overload default blueprint") {
        val blueprint = ChromeBlueprint.default &&
          ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(blueprint)("pageLoadStrategy", "eager")
      },
      test("Chrome Blueprint can compose two action") {
        val blueprint = ChromeBlueprint.noGpu and ChromeBlueprint.noExtensions
        for {
          a1 <- assertArgument(blueprint)("--disable-gpu")
          a2 <- assertArgument(blueprint)("--disable-extensions")
        } yield a1 && a2

      },
      test("Chrome Blueprint can set capability") {
        val blueprint = CommonBlueprint.setCapability("key", "value") && ChromeBlueprint.default
        assertCapability(blueprint)("key", "value")
      },
      test("Chrome Blueprint can set another pageLoadStrategy") {
        assertCapability(ChromeBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER))("pageLoadStrategy", "eager")
      },
      test("Chrome Blueprint can add an argument") {
        assertArgument(ChromeBlueprint.addArgument("--argument"))("--argument")
      },
      test("Chrome Blueprint can add arguments") {
        val blueprint = ChromeBlueprint.addArguments(List("--argument", "--argument2"))
        for {
          a1 <- assertArgument(blueprint)("--argument")
          a2 <- assertArgument(blueprint)("--argument2")
        } yield a1 && a2
      },
      test("Chrome Blueprint can disable gpu") {
        assertArgument(ChromeBlueprint.noGpu)("--disable-gpu")
      },
      test("Chrome Blueprint can disable extensions") {
        assertArgument(ChromeBlueprint.noExtensions)("--disable-extensions")
      },
      test("Chrome Blueprint can disable popup blocking") {
        assertArgument(ChromeBlueprint.noPopupBlocking)("--disable-popup-blocking")
      },
      test("Chrome Blueprint can start fullscreen") {
        assertArgument(ChromeBlueprint.fullscreen)("--start-fullscreen")
      },
      test("Chrome Blueprint can be headless") {
        assertArgument(ChromeBlueprint.headless)("--headless")
      }
    )

  def spec: Spec[Any, TestFailure[Any], TestSuccess] = suite("Chrome Blueprint Spec")(suiteChromeBlueprint)
}
