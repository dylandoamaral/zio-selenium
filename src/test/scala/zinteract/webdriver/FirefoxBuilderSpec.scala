package zinteract.test

import zio.ZIO
import zio.test._
import zio.test.Assertion._

import zinteract.webdriver.BuilderOps.firefox
import zinteract.webdriver.FirefoxBlueprintOps
import zinteract.webdriver.FirefoxBlueprintOps.FirefoxBlueprint

import scala.jdk.CollectionConverters._
import org.openqa.selenium.{PageLoadStrategy}

object FirefoxBuilderSpec extends DefaultRunnableSpec {
  def assertCapability(blueprint: FirefoxBlueprint)(key: String, value: String) =
    for {
      capabilities <- (firefox using blueprint).buildOptions.map(_.asMap.asScala)
    } yield assert(capabilities.get(key).map(_.toString))(isSome(equalTo(value)))

  def assertArgument(blueprint: FirefoxBlueprint)(argument: String) =
    for {
      options <- (firefox using blueprint).buildOptions
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

  def suiteFirefoxBuilder =
    suite("Firefox Builder Spec")(
      test("We can build an unit firefox builder") {
        val builder = firefox
        assert(builder.path)(equalTo(""))
      },
      test("We can update the path of a build with 'at'") {
        val builder = firefox at "path"
        assert(builder.path)(equalTo("path"))
      },
      test("We can update the path of a build with '>'") {
        val builder = firefox > "path"
        assert(builder.path)(equalTo("path"))
      },
      testM("We can update the blueprint of a build with 'using'") {
        val builder = firefox using FirefoxBlueprintOps.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      },
      testM("We can update the blueprint of a build with '>>'") {
        val builder = firefox >> FirefoxBlueprintOps.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(builder.blueprint)("pageLoadStrategy", "eager")
      }
    )

  def suiteFirefoxBlueprint =
    suite("Firefox Default Blueprint Spec")(
      testM("Firefox Blueprint can overload default blueprint") {
        val blueprint = FirefoxBlueprintOps.default &&
          FirefoxBlueprintOps.setLoadPageStrategy(PageLoadStrategy.EAGER)
        assertCapability(blueprint)("pageLoadStrategy", "eager")
      }
    )

  def spec = suite("Firefox Builder Spec")(suiteFirefoxBuilder, suiteFirefoxBlueprint)
}
