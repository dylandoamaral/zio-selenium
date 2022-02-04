package zinteract.builder

import zio.test.Assertion._
import zio.test._
import zinteract.builder.FirefoxBlueprint.FirefoxBlueprint
import org.openqa.selenium.PageLoadStrategy
import zio.ZIO

import java.util
import scala.jdk.CollectionConverters._

object FirefoxBuilderSpec extends DefaultRunnableSpec {
  def assertCapability(blueprint: FirefoxBlueprint)(key: String, value: String): ZIO[Any, Throwable, TestResult] =
    for {
      capabilities <- (firefox using blueprint).buildOptions.map(_.asMap.asScala)
    } yield assert(capabilities.get(key).map(_.toString))(isSome(equalTo(value)))

  def assertArgument(blueprint: FirefoxBlueprint)(argument: String): ZIO[Any, Throwable, TestResult] =
    for {
      options <- (firefox using blueprint).buildOptions
      arguments <- ZIO.succeed({
        val field = options.getClass.getDeclaredField("firefoxOptions")
        field.setAccessible(true)
        field
          .get(options)
          .asInstanceOf[util.Map[String, util.ArrayList[String]]]
          .values
          .asScala
          .head
          .asScala
          .toList
      })
    } yield assert(arguments)(contains(argument))

  def suiteFirefoxBuilder: Spec[Any, TestFailure[Throwable], TestSuccess] =
    suite("Firefox Builder Spec")(
      test("We can build an unit firefox builder") {
        val builder = firefox
        assert(builder.path)(isNone)
      },
      test("We can update the path of a build with 'at'") {
        val builder = firefox at "path"
        assert(builder.path)(isSome(equalTo("path")))
      },
      test("We can update the path of a build with '>'") {
        val builder = firefox > "path"
        assert(builder.path)(isSome(equalTo("path")))
      },
      test("We can update the blueprint of a build with 'using'") {
        val builder = firefox using FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      },
      test("We can update the blueprint of a build with '>>'") {
        val builder = firefox >> FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      }
    )

  def suiteFirefoxBlueprint: Spec[Any, TestFailure[Throwable], TestSuccess] =
    suite("Firefox Default Blueprint Spec")(
      test("Firefox Blueprint can overload default blueprint") {
        val blueprint = FirefoxBlueprint.default &&
          FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(blueprint)("pageLoadStrategy", "eager")
      },
      test("Firefox Blueprint can compose two action") {
        val blueprint = FirefoxBlueprint.addArgument("--argument") and FirefoxBlueprint.addArgument("--argument2")

        for {
          a1 <- assertArgument(blueprint)("--argument")
          a2 <- assertArgument(blueprint)("--argument2")
        } yield a1 && a2
      },
      test("Firefox Blueprint can set capability") {
        val blueprint = CommonBlueprint.setCapability("key", "value") && FirefoxBlueprint.default
        assertCapability(blueprint)("key", "value")
      },
      test("Firefox Blueprint can set another pageLoadStrategy") {
        assertCapability(FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER))("pageLoadStrategy", "eager")
      },
      test("Firefox Blueprint can add an argument") {
        assertArgument(FirefoxBlueprint.addArgument("--argument"))("--argument")
      },
      test("Firefox Blueprint can add arguments") {
        val blueprint = FirefoxBlueprint.addArguments(List("--argument", "--argument2"))

        for {
          a1 <- assertArgument(blueprint)("--argument")
          a2 <- assertArgument(blueprint)("--argument2")
        } yield a1 && a2
      }
    )

  def spec: Spec[Any, TestFailure[Throwable], TestSuccess] =
    suite("Firefox Builder Spec")(suiteFirefoxBuilder, suiteFirefoxBlueprint)
}
