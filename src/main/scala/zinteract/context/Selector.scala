package zinteract.context

import org.openqa.selenium.By

object Selector {
  sealed trait SearchingMethod { def symbol: String }
  case object Equal     extends SearchingMethod { def symbol: String = ""  }
  case object Contains  extends SearchingMethod { def symbol: String = "*" }
  case object StartWith extends SearchingMethod { def symbol: String = "^" }
  case object EndWith   extends SearchingMethod { def symbol: String = "$" }

  sealed trait Selector {
    def and(that: Selector)       = AndSelector(this, that)
    def after(that: Selector)     = AfterSelector(that, this)
    def before(that: Selector)    = BeforeSelector(this, that)
    def inside(that: Selector)    = InsideSelector(that, this)
    def childOf(parent: Selector) = ChildSelector(parent, this)
  }
  sealed trait AttributeSelector extends Selector {
    def in(tagS: TagSelector) = ElementSelector(tagS, this)
  }
  sealed trait TagSelector extends Selector {
    def flags: Set[FlagSelector]                  = Set.empty
    def containing(attributeS: AttributeSelector) = ElementSelector(this, attributeS)
    def being(flagS: FlagSelector)                = TagSelectorLink(flags + flagS)
  }

  case class FlagSelector(flag: String)     extends Selector
  case class TagSelectorCommon(tag: String) extends TagSelector
  case class TagSelectorLink(override val flags: Set[FlagSelector] = Set.empty) extends TagSelector {
    def ifVisited   = this being Flag.visited
    def ifUnvisited = this being Flag.unvisited
    def ifHovered   = this being Flag.hovered
    def ifActive    = this being Flag.active
  }
  case class InputSelectorLink(override val flags: Set[FlagSelector] = Set.empty) extends TagSelector {
    def ifChecked       = this being Flag.checked
    def ifDefault       = this being Flag.default
    def ifDisabled      = this being Flag.disabled
    def ifEnabled       = this being Flag.enabled
    def ifFocus         = this being Flag.focused
    def ifInRange       = this being Flag.inRange
    def ifIndeterminate = this being Flag.indeterminate
    def ifInvalid       = this being Flag.invalid
    def ifOptimal       = this being Flag.optimal
    def ifOutOfRange    = this being Flag.outOfRange
    def ifPlaceholder   = this being Flag.placeholder
    def ifReadOnly      = this being Flag.readOnly
    def ifNotReadWrite  = this being Flag.notReadWrite
    def ifRequired      = this being Flag.required
    def ifValid         = this being Flag.valid

  }
  case class PSelectorLink(override val flags: Set[FlagSelector] = Set.empty) extends TagSelector {
    def ifLang(lang: String)    = this being Flag.lang(lang)
    def ifEmpty                 = this being Flag.empty
    def ifFirstChild            = this being Flag.firstChild
    def ifFirstOfType           = this being Flag.firstOfType
    def ifLastChild             = this being Flag.lastChild
    def ifLastOfType            = this being Flag.lastOfType
    def ifNthChild(n: Int)      = this being Flag.nthChild(n)
    def ifNthLastChild(n: Int)  = this being Flag.nthLastChild(n)
    def ifNthLastOfType(n: Int) = this being Flag.nthLastOfType(n)
    def ifNthOfType(n: Int)     = this being Flag.nthOfType(n)
    def ifOnlyOfType            = this being Flag.onlyOfType
    def ifOnlyChild             = this being Flag.onlyChild
  }
  case class AttributeSelectorAlone(attribute: String) extends AttributeSelector {
    def equalsTo(value: String)   = AttributeSelectorValue(attribute, Equal, value)
    def contains(value: String)   = AttributeSelectorValue(attribute, Contains, value)
    def startsWith(value: String) = AttributeSelectorValue(attribute, StartWith, value)
    def endsWith(value: String)   = AttributeSelectorValue(attribute, EndWith, value)
  }
  case class AttributeSelectorValue(attribute: String, method: SearchingMethod, value: String) extends AttributeSelector
  case class ElementSelector(tagS: TagSelector, attributeS: AttributeSelector)                 extends Selector
  case class AndSelector(first: Selector, second: Selector)                                    extends Selector
  case class AfterSelector(previous: Selector, after: Selector)                                extends Selector
  case class BeforeSelector(previous: Selector, after: Selector)                               extends Selector
  case class ChildSelector(parent: Selector, child: Selector)                                  extends Selector
  case class InsideSelector(container: Selector, content: Selector)                            extends Selector

  def interpretTag(tag: String, flags: Set[FlagSelector], ignoreTag: Boolean = false): String =
    tag + (if (ignoreTag) "" else interpretFlags(flags))

  def interpretFlags(flags: Set[FlagSelector]): String = flags.map(interpret(_)).mkString

  def interpret(selector: Selector, ignoreTag: Boolean = false): String =
    selector match {
      case FlagSelector(flag)                               => s":$flag"
      case TagSelectorCommon(tag)                           => interpretTag(tag, Set.empty, ignoreTag)
      case TagSelectorLink(flags)                           => interpretTag("a", flags, ignoreTag)
      case InputSelectorLink(flags)                         => interpretTag("input", flags, ignoreTag)
      case PSelectorLink(flags)                             => interpretTag("p", flags, ignoreTag)
      case AttributeSelectorAlone(attribute)                => s"[$attribute]"
      case AttributeSelectorValue(attribute, method, value) => s"[$attribute${method.symbol}=$value]"
      case ElementSelector(tagS, attributeS) =>
        interpret(tagS, true) + interpret(attributeS) + interpretFlags(tagS.flags)
      case AndSelector(first, second)         => interpret(first) + "," + interpret(second)
      case AfterSelector(previous, after)     => interpret(previous) + "+" + interpret(after)
      case BeforeSelector(previous, after)    => interpret(after) + "~" + interpret(previous)
      case ChildSelector(parent, child)       => interpret(parent) + ">" + interpret(child)
      case InsideSelector(container, content) => interpret(container) + " " + interpret(content)
    }

  def by(selector: Selector): By = By.className(interpret(selector))

  object Tag {
    val a          = TagSelectorLink()
    val body       = TagSelectorCommon("body")
    val br         = TagSelectorCommon("br")
    val button     = TagSelectorCommon("button")
    val canvas     = TagSelectorCommon("canvas")
    val caption    = TagSelectorCommon("caption")
    val div        = TagSelectorCommon("div")
    val form       = TagSelectorCommon("form")
    val footer     = TagSelectorCommon("footer")
    val fullscreen = TagSelectorCommon(":fullscreen")
    val h1         = TagSelectorCommon("h1")
    val h2         = TagSelectorCommon("h2")
    val h3         = TagSelectorCommon("h3")
    val h4         = TagSelectorCommon("h4")
    val h5         = TagSelectorCommon("h5")
    val h6         = TagSelectorCommon("h6")
    val head       = TagSelectorCommon("head")
    val header     = TagSelectorCommon("header")
    val hr         = TagSelectorCommon("hr")
    val html       = TagSelectorCommon("html")
    val iframe     = TagSelectorCommon("iframe")
    val img        = TagSelectorCommon("img")
    val input      = InputSelectorLink()
    val li         = TagSelectorCommon("li")
    val link       = TagSelectorCommon("link")
    val main       = TagSelectorCommon("main")
    val meta       = TagSelectorCommon("meta")
    val nav        = TagSelectorCommon("nav")
    val ol         = TagSelectorCommon("ol")
    val p          = PSelectorLink()
    val table      = TagSelectorCommon("table")
    val title      = TagSelectorCommon("title")
    val root       = TagSelectorCommon(":root")
    val script     = TagSelectorCommon("script")
    val span       = TagSelectorCommon("span")
    val style      = TagSelectorCommon("style")
    val ul         = TagSelectorCommon("ul")
  }

  object Attribute {
    val Class   = AttributeSelectorAlone("class")
    val content = AttributeSelectorAlone("content")
    val href    = AttributeSelectorAlone("href")
    val id      = AttributeSelectorAlone("id")
    val name    = AttributeSelectorAlone("name")
    val rel     = AttributeSelectorAlone("rel")
    val src     = AttributeSelectorAlone("src")
    val style   = AttributeSelectorAlone("style")
    val Type    = AttributeSelectorAlone("type")
  }

  object Flag {
    val visited               = FlagSelector("visited")
    val unvisited             = FlagSelector("link")
    val hovered               = FlagSelector("hover")
    val active                = FlagSelector("active")
    val checked               = FlagSelector("checked")
    val default               = FlagSelector("default")
    val disabled              = FlagSelector("disabled")
    val enabled               = FlagSelector("enabled")
    val focused               = FlagSelector("focus")
    val inRange               = FlagSelector("in-range")
    val indeterminate         = FlagSelector("inderterminate")
    val invalid               = FlagSelector("invalid")
    val optimal               = FlagSelector("optimal")
    val outOfRange            = FlagSelector("out-of-range")
    val placeholder           = FlagSelector(":placeholder")
    val readOnly              = FlagSelector("read-only")
    val notReadWrite          = FlagSelector("read-write")
    val required              = FlagSelector("required")
    val valid                 = FlagSelector("valid")
    def lang(lang: String)    = FlagSelector(s"lang($lang)")
    val empty                 = FlagSelector("empty")
    val firstChild            = FlagSelector("first-child")
    val firstOfType           = FlagSelector("first-of-type")
    val lastChild             = FlagSelector("last-child")
    val lastOfType            = FlagSelector("last-of-type")
    def nthChild(n: Int)      = FlagSelector(s"nth-child($n)")
    def nthLastChild(n: Int)  = FlagSelector(s"nth-last-child($n)")
    def nthLastOfType(n: Int) = FlagSelector(s"nth-last-of-type($n)")
    def nthOfType(n: Int)     = FlagSelector(s"nth-of-type($n)")
    val onlyOfType            = FlagSelector("only-of-type")
    val onlyChild             = FlagSelector("only-child")
  }
}
