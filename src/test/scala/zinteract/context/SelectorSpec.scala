package zinteract.context

import zinteract.TestDriver.testLayer
import zio.test.Assertion._
import zio.test._
import zinteract.context.Selector._
import zinteract.element._
import zinteract.webdriver
import zio.ZEnv

object SelectorSpec extends DefaultRunnableSpec {
  val flagSelectorPath: String      = getClass.getResource("/FlagSelectorSpec.html").getPath
  val tagSelectorPath: String       = getClass.getResource("/TagSelectorSpec.html").getPath
  val attributeSelectorPath: String = getClass.getResource("/AttributeSelectorSpec.html").getPath
  val logicSelectorPath: String     = getClass.getResource("/LogicSelectorSpec.html").getPath
  val testWebsite: String => String = (path: String) => s"file://$path"

  def suiteSelector: Spec[Any, TestFailure[Nothing], TestSuccess] =
    suite("Selector Spec")(
      test("Methods 'in' and 'containing' are similar and reverse methods") {
        val attribute  = id equalsTo "hello"
        val tag        = input being checked
        val selector   = attribute in tag
        val equivalent = tag containing attribute
        assert(interpret(selector))(equalTo(interpret(equivalent)))
      }
    )

  def testTag(tagSelector: TagSelector, maybeTag: Option[String] = None): ZSpec[ZEnv, Throwable] =
    test(s"${tagSelector.tag.capitalize} is a tag selector") {
      val selector = tagSelector

      val effect = for {
        _       <- webdriver.link(testWebsite(tagSelectorPath))
        element <- webdriver.findElement(by(selector))
        tag     <- element.getTagNameM
      } yield assert(tag)(equalTo(maybeTag.getOrElse(tagSelector.tag)))

      effect.provideCustomLayer(testLayer(false, true))
    }

  def suiteTagSelector: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
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

  def testAttribute(
      attributeSelector: AttributeSelectorAlone,
      maybeAttribute: Option[String] = None
  ): ZSpec[ZEnv, Throwable] =
    test(s"${attributeSelector.attribute.capitalize} is an attribute selector") {
      val selector = attributeSelector
      val value    = maybeAttribute.getOrElse(attributeSelector.attribute)

      val effect = for {
        _       <- webdriver.link(testWebsite(attributeSelectorPath))
        element <- webdriver.findElement(by(selector))
        _       <- element.getAttributeM(value)
      } yield assertCompletes

      effect.provideCustomLayer(testLayer(false, true))
    }

  def testAttributeSearchingMethod(attributeSelector: AttributeSelectorValue, result: String): ZSpec[ZEnv, Throwable] =
    test(f"Attribute selector can select an attribute using $result") {
      val selector = attributeSelector

      val effect = for {
        _       <- webdriver.link(testWebsite(attributeSelectorPath))
        element <- webdriver.findElement(by(selector))
        id      <- element.getAttributeM("id")
      } yield assert(id)(equalTo(result))

      effect.provideCustomLayer(testLayer(false, true))
    }

  def suiteAttributeSelector: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
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

  def testFlag(tagSelector: TagSelector, flagSelector: FlagSelector): ZSpec[ZEnv, Throwable] =
    test(s"${tagSelector.tag.capitalize} Tag Selector can only select ${flagSelector.flag} ${tagSelector.tag}") {
      val selector = tagSelector being flagSelector

      val effect = for {
        _       <- webdriver.link(testWebsite(flagSelectorPath))
        element <- webdriver.findElement(by(selector))
        id      <- element.getAttributeM("id")
      } yield assert(id)(equalTo(flagSelector.flag))

      effect.provideCustomLayer(testLayer(false, true))
    }

  def suiteFlagSelector: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
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

  def suiteLogicSelector: Spec[ZEnv, TestFailure[Throwable], TestSuccess] =
    suite("Logic Selector Spec")(
      test("And selector can select all h1 and h2 elements") {
        val selector = h1 and h2

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(8))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("After selector can select all h2 after h1 elements") {
        val selector = h2 after h1

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(2))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("Inside selector can select all h2 inside div elements") {
        val selector = h2 inside div

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(3))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("ChildOf selector can select all h2 inside div elements") {
        val selector = h2 childOf div

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(3))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      },
      test("Not selector can select all not h2") {
        val selector = h2.not

        val effect = for {
          _        <- webdriver.link(testWebsite(logicSelectorPath))
          elements <- webdriver.findElements(by(selector))
        } yield assert(elements.length)(equalTo(9))

        effect.provideCustomLayer(testLayer(jsEnabled = true))
      }
    )

  def spec: Spec[ZEnv, TestFailure[Throwable], TestSuccess] = suite("Selector Spec")(
    suiteSelector,
    suiteTagSelector,
    suiteAttributeSelector,
    suiteFlagSelector,
    suiteLogicSelector
  )
}
