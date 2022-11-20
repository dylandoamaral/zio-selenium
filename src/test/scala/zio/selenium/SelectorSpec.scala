package zio.selenium

import org.openqa.selenium.WebDriverException

import zio.selenium.Selector._
import zio.test._
import zio.test.TestAspect.sequential

object SelectorSpec extends SharedWebDriverWithCssSpec {
  val flagSelectorPath: String      = getClass.getResource("/FlagSelectorSpec.html").getPath
  val tagSelectorPath: String       = getClass.getResource("/TagSelectorSpec.html").getPath
  val attributeSelectorPath: String = getClass.getResource("/AttributeSelectorSpec.html").getPath
  val logicSelectorPath: String     = getClass.getResource("/LogicSelectorSpec.html").getPath
  val testWebsite: String => String = (path: String) => s"file://$path"

  def suiteSelector: Spec[WebDriver, WebDriverException] =
    suite("Selector Spec")(
      test("Methods 'in' and 'containing' are similar and reverse methods") {
        val attribute  = id equalsTo "hello"
        val tag        = input being checked
        val selector   = attribute in tag
        val equivalent = tag containing attribute
        assertTrue(interpret(selector) == interpret(equivalent))
      }
    )

  def testTag(tagSelector: TagSelector, maybeTag: Option[String] = None): Spec[WebDriver, WebDriverException] =
    test(s"${tagSelector.tag.capitalize} is a tag selector") {
      val selector = tagSelector

      for {
        _       <- WebDriver.get(testWebsite(tagSelectorPath))
        element <- WebDriver.findElement(by(selector))
        tag     <- element.getTagName
      } yield assertTrue(tag == maybeTag.getOrElse(tagSelector.tag))

    }

  def suiteTagSelector: Spec[WebDriver, WebDriverException] =
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
    ) @@ sequential

  def testAttribute(
      attributeSelector: AttributeSelectorAlone,
      maybeAttribute: Option[String] = None
  ): Spec[WebDriver, WebDriverException] =
    test(s"${attributeSelector.attribute.capitalize} is an attribute selector") {
      val selector = attributeSelector
      val value    = maybeAttribute.getOrElse(attributeSelector.attribute)

      for {
        _       <- WebDriver.get(testWebsite(attributeSelectorPath))
        element <- WebDriver.findElement(by(selector))
        _       <- element.getAttribute(value)
      } yield assertCompletes
    }

  def testAttributeSearchingMethod(
      attributeSelector: AttributeSelectorValue,
      result: String
  ): Spec[WebDriver, WebDriverException] =
    test(f"Attribute selector can select an attribute using $result") {
      val selector = attributeSelector

      for {
        _       <- WebDriver.get(testWebsite(attributeSelectorPath))
        element <- WebDriver.findElement(by(selector))
        id      <- element.getAttribute("id")
      } yield assertTrue(id.contains(result))
    }

  def suiteAttributeSelector: Spec[WebDriver, WebDriverException] =
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
    ) @@ sequential

  def testFlag(tagSelector: TagSelector, flagSelector: FlagSelector): Spec[WebDriver, WebDriverException] =
    test(s"${tagSelector.tag.capitalize} Tag Selector can only select ${flagSelector.flag} ${tagSelector.tag}") {
      val selector = tagSelector being flagSelector

      for {
        _       <- WebDriver.get(testWebsite(flagSelectorPath))
        element <- WebDriver.findElement(by(selector))
        id      <- element.getAttribute("id")
      } yield assertTrue(id.contains(flagSelector.flag))
    }

  def suiteFlagSelector: Spec[WebDriver, WebDriverException] =
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
    ) @@ sequential

  def suiteLogicSelector: Spec[WebDriver, WebDriverException] =
    suite("Logic Selector Spec")(
      test("And selector can select all h1 and h2 elements") {
        val selector = h1 and h2

        for {
          _        <- WebDriver.get(testWebsite(logicSelectorPath))
          elements <- WebDriver.findElements(by(selector))
        } yield assertTrue(elements.length == 8)
      },
      test("After selector can select all h2 after h1 elements") {
        val selector = h2 after h1

        for {
          _        <- WebDriver.get(testWebsite(logicSelectorPath))
          elements <- WebDriver.findElements(by(selector))
        } yield assertTrue(elements.length == 2)
      },
      test("Inside selector can select all h2 inside div elements") {
        val selector = h2 inside div

        for {
          _        <- WebDriver.get(testWebsite(logicSelectorPath))
          elements <- WebDriver.findElements(by(selector))
        } yield assertTrue(elements.length == 3)
      },
      test("ChildOf selector can select all h2 inside div elements") {
        val selector = h2 childOf div

        for {
          _        <- WebDriver.get(testWebsite(logicSelectorPath))
          elements <- WebDriver.findElements(by(selector))
        } yield assertTrue(elements.length == 3)

      },
      test("Not selector can select all not h2") {
        val selector = h2.not

        for {
          _        <- WebDriver.get(testWebsite(logicSelectorPath))
          elements <- WebDriver.findElements(by(selector))
        } yield assertTrue(elements.length == 9)
      }
    ) @@ sequential

  def spec: Spec[WebDriver, WebDriverException] =
    suite("Selector Spec")(
      suiteSelector,
      suiteTagSelector,
      suiteAttributeSelector,
      suiteFlagSelector,
      suiteLogicSelector
    ) @@ sequential
}
