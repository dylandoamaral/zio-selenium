package zinteract.test

import zio.test._
import zio.test.Assertion._

import zinteract.test.TestDriver.testLayer
import zinteract.webdriver
import zinteract.element._
import zinteract.context.Selector._

object SelectorSpec extends DefaultRunnableSpec {
  val flagSelectorPath      = getClass.getResource("/FlagSelectorSpec.html").getPath
  val tagSelectorPath       = getClass.getResource("/TagSelectorSpec.html").getPath
  val attributeSelectorPath = getClass.getResource("/AttributeSelectorSpec.html").getPath
  val logicSelectorPath     = getClass.getResource("/LogicSelectorSpec.html").getPath
  val testWebsite           = (path: String) => s"file://$path"

  def suiteSelector =
    suite("Selector Spec")(
      test("Methods 'in' and 'containing' are similar and reverse methods") {
        val attribute  = id equalsTo "hello"
        val tag        = input being checked
        val selector   = attribute in tag
        val equivalent = tag containing attribute
        assert(interpret(selector))(equalTo(interpret(equivalent)))
      }
    )

  def testTag(tagSelector: TagSelector, maybeTag: Option[String] = None) =
    testM(s"${tagSelector.tag.capitalize} is a tag selector") {
      val selector = tagSelector

      val effect = for {
        _       <- webdriver.link(testWebsite(tagSelectorPath))
        element <- webdriver.findElement(by(selector))
        tag     <- element.getTagNameM
      } yield assert(tag)(equalTo(maybeTag.getOrElse(tagSelector.tag)))

      effect.provideCustomLayer(testLayer(false, true))
    }

  def suiteTagSelector =
    suite("Tag Selector Spec")(
      testTag(a),
      testTag(any, Some("html")),
      testTag(body),
      testTag(br),
      testTag(button),
      testTag(canvas),
      testTag(div),
      testTag(form),
      testTag(footer),
      testTag(h1),
      testTag(h2),
      testTag(h3),
      testTag(h4),
      testTag(h5),
      testTag(h6),
      testTag(head),
      testTag(header),
      testTag(hr),
      testTag(html),
      testTag(iframe),
      testTag(img),
      testTag(input),
      testTag(li),
      testTag(link),
      testTag(meta),
      testTag(nav),
      testTag(ol),
      testTag(p),
      testTag(table),
      testTag(title),
      testTag(root, Some("html")),
      testTag(script),
      testTag(span),
      testTag(ul)
    )

  def testAttribute(attributeSelector: AttributeSelectorAlone, maybeAttribute: Option[String] = None) =
    testM(s"${attributeSelector.attribute.capitalize} is an attribute selector") {
      val selector = attributeSelector
      val value    = maybeAttribute.getOrElse(attributeSelector.attribute)

      val effect = for {
        _         <- webdriver.link(testWebsite(attributeSelectorPath))
        element   <- webdriver.findElement(by(selector))
        attribute <- element.getAttributeM(value)
      } yield assertCompletes

      effect.provideCustomLayer(testLayer(false, true))
    }

  def testAttributeSearchingMethod(attributeSelector: AttributeSelectorValue, result: String) =
    testM(f"Attribute selector can select an attribute using $result") {
      val selector = attributeSelector

      val effect = for {
        _       <- webdriver.link(testWebsite(attributeSelectorPath))
        element <- webdriver.findElement(by(selector))
        id      <- element.getAttributeM("id")
      } yield assert(id)(equalTo(result))

      effect.provideCustomLayer(testLayer(false, true))
    }

  def suiteAttributeSelector =
    suite("Attribute Selector Spec")(
      testAttribute(klass, Some("class")),
      testAttribute(content),
      testAttribute(href),
      testAttribute(id),
      testAttribute(name),
      testAttribute(rel),
      testAttribute(src),
      testAttribute(style),
      testAttribute(tipe, Some("type")),
      testAttributeSearchingMethod(klass contains "test", "contains"),
      testAttributeSearchingMethod(klass startsWith "test", "startsWith"),
      testAttributeSearchingMethod(klass endsWith "test", "endsWith")
    )

  def testFlag(tagSelector: TagSelector, flagSelector: FlagSelector) =
    testM(s"${tagSelector.tag.capitalize} Tag Selector can only select ${flagSelector.flag} ${tagSelector.tag}") {
      val selector = tagSelector being flagSelector

      val effect = for {
        _       <- webdriver.link(testWebsite(flagSelectorPath))
        element <- webdriver.findElement(by(selector))
        id      <- element.getAttributeM("id")
      } yield assert(id)(equalTo(flagSelector.flag))

      effect.provideCustomLayer(testLayer(false, true))
    }

  def suiteFlagSelector =
    suite("Flag Selector Spec")(
      testFlag(input, checked),
      testFlag(input, disabled),
      testFlag(input, enabled),
      testFlag(input, required),
      testFlag(p, lang("it")),
      testFlag(p, empty),
      testFlag(p, firstChild),
      testFlag(h1, firstOfType),
      testFlag(p, lastChild),
      testFlag(h1, lastOfType),
      testFlag(p, nthChild(2)),
      testFlag(h1, nthOfType(2)),
      testFlag(p, nthLastChild(2)),
      testFlag(h1, nthLastOfType(2)),
      testFlag(img, onlyOfType),
      testFlag(h2, onlyChild)
    )

  def suiteLogicSelector =
    suite("Logic Selector Spec")(
      testM("And selector can select all h1 and h2 elements") {
        val selector = h1 and h2

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(8))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("After selector can select all h2 after h1 elements") {
        val selector = h2 after h1

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(2))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Inside selector can select all h2 inside div elements") {
        val selector = h2 inside div

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(3))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("ChildOf selector can select all h2 inside div elements") {
        val selector = h2 childOf div

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(3))

        effect.provideCustomLayer(testLayer(false, true))
      },
      testM("Not selector can select all not h2") {
        val selector = h2.not

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(9))

        effect.provideCustomLayer(testLayer(false, true))
      }
    )

  def spec = suite("Selector Spec")(
    suiteSelector,
    suiteTagSelector,
    suiteAttributeSelector,
    suiteFlagSelector,
    suiteLogicSelector
  )
}
