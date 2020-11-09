package zinteract.test

import zio.ZIO
import zio.test._
import zio.test.Assertion._

import zinteract.webdriver.Blueprint
import zinteract.webdriver.RemoteBuilder

import org.openqa.selenium.MutableCapabilities

import scala.jdk.CollectionConverters._

object BuilderUtils {
  def assertArgument[Driver, Options <: MutableCapabilities](
      builder: RemoteBuilder[Options, Driver],
      blueprint: Blueprint[Options]
  )(argument: String) =
    for {
      options <- (builder using blueprint).buildOptions
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

}
