package zinteract.builder

import zio.ZIO
import zio.test.Assertion._
import zio.test._

import org.openqa.selenium.MutableCapabilities

import scala.jdk.CollectionConverters._

object BuilderUtils {
  def assertArgument[Driver, Options <: MutableCapabilities](
      builder: RemoteBuilder[Options],
      blueprint: Blueprint[Options]
  )(argument: String): ZIO[Any, Throwable, TestResult] =
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
