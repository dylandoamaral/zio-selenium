package zinteract.test

import zio.test._
import zio.test.Assertion._

import zinteract.builder.firefox
import zinteract.builder.CommonBlueprint
import zinteract.builder.FirefoxBlueprint
import zinteract.builder.FirefoxBlueprint.FirefoxBlueprint

import org.openqa.selenium.{PageLoadStrategy}

import scala.jdk.CollectionConverters._

object FirefoxBuilderSpec extends DefaultRunnableSpec {
  def assertCapability(blueprint: FirefoxBlueprint)(key: String, value: String) =
    for {
      capabilities <- (firefox using blueprint).buildOptions.map(_.asMap.asScala)
    } yield assert(capabilities.get(key).map(_.toString))(isSome(equalTo(value)))

  def assertArgument(blueprint: FirefoxBlueprint)(argument: String) =
    BuilderUtils.assertArgument(firefox, blueprint)(argument)

  def suiteFirefoxBuilder =
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
      testM("We can update the blueprint of a build with 'using'") {
        val builder = firefox using FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      },
      testM("We can update the blueprint of a build with '>>'") {
        val builder = firefox >> FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      }
    )

  def suiteFirefoxBlueprint =
    suite("Firefox Default Blueprint Spec")(
      testM("Firefox Blueprint can overload default blueprint") {
        val blueprint = FirefoxBlueprint.default &&
          FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(blueprint)("pageLoadStrategy", "eager")
      },
      testM("Firefox Blueprint can compose two action") {
        val blueprint = FirefoxBlueprint.addArgument("--argument") and FirefoxBlueprint.addArgument("--argument2")
        assertArgument(blueprint)("--argument")
        assertArgument(blueprint)("--argument2")
      },
      testM("Firefox Blueprint can set capability") {
        val blueprint = CommonBlueprint.setCapability("key", "value") && FirefoxBlueprint.default
        assertCapability(blueprint)("key", "value")
      },
      testM("Firefox Blueprint can set another pageLoadStrategy") {
        assertCapability(FirefoxBlueprint.setLoadPageStrategy(PageLoadStrategy.EAGER))("pageLoadStrategy", "eager")
      },
      testM("Firefox Blueprint can add an argument") {
        assertArgument(FirefoxBlueprint.addArgument("--argument"))("--argument")
      },
      testM("Firefox Blueprint can add arguments") {
        val blueprint = FirefoxBlueprint.addArguments(List("--argument", "--argument2"))
        assertArgument(blueprint)("--argument")
        assertArgument(blueprint)("--argument2")
      }
    )

  def spec = suite("Firefox Builder Spec")(suiteFirefoxBuilder, suiteFirefoxBlueprint)
}
