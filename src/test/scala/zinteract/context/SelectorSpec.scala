package zinteract.test

import zio.test._
import zio.test.Assertion._

import zinteract.context.Selector.interpret
import zinteract.context.Selector.Tag._
import zinteract.context.Selector.Attribute._
import zinteract.context.Selector.Flag._

object SelectorSpec extends DefaultRunnableSpec {
  val testPath    = getClass.getResource("/SelectorSpec.html").getPath
  val testWebsite = s"file://$testPath"

  def suiteSelector =
    suite("Selector Spec")(
      test("Methods 'in' and 'containing' are similar and reverse methods") {
        val attribute  = id equalsTo "hello"
        val tag        = a being unvisited being hovered
        val selector   = attribute in tag
        val equivalent = tag containing attribute
        assert(interpret(selector))(equalTo(interpret(equivalent)))
      }
    )

  def suiteTagSelector =
    suite("Selector Spec")(
      test("Div is a tag selector") {
        assert(interpret(div))(equalTo("div"))
      },
      test("A is a tag selector") {
        assert(interpret(a))(equalTo("a"))
      }
    )

  def spec = suite("Selector Spec")(suiteSelector, suiteTagSelector)
}
