package zinteract.context

import org.openqa.selenium.By

object Selector {
  sealed trait SearchingMethod { def symbol: String }
  case object Equal     extends SearchingMethod { def symbol: String = ""  }
  case object Contains  extends SearchingMethod { def symbol: String = "*" }
  case object StartWith extends SearchingMethod { def symbol: String = "^" }
  case object EndWith   extends SearchingMethod { def symbol: String = "$" }

  sealed trait Selector {
    def and(that: Selector)       = AndSelector(that, this)
    def after(that: Selector)     = AfterSelector(that, this)
    def inside(that: Selector)    = InsideSelector(that, this)
    def childOf(parent: Selector) = ChildSelector(parent, this)
    def not                       = NotSelector(this)
  }
  sealed trait AttributeSelector extends Selector {
    def in(tagS: TagSelector) = ElementSelector(tagS, this)
  }

  case class FlagSelector(flag: String) extends Selector
  case class TagSelector(tag: String, flags: Set[FlagSelector] = Set.empty) extends Selector {
    def being(flagS: FlagSelector)                = TagSelector(tag, flags + flagS)
    def containing(attributeS: AttributeSelector) = ElementSelector(this, attributeS)
  }
  case class AttributeSelectorAlone(attribute: String) extends AttributeSelector {
    def equalsTo(value: String)   = AttributeSelectorValue(attribute, Equal, value)
    def contains(value: String)   = AttributeSelectorValue(attribute, Contains, value)
    def startsWith(value: String) = AttributeSelectorValue(attribute, StartWith, value)
    def endsWith(value: String)   = AttributeSelectorValue(attribute, EndWith, value)
  }
  case class AttributeSelectorValue(attribute: String, method: SearchingMethod, value: String) extends AttributeSelector
  case class ElementSelector(tagS: TagSelector, attributeS: AttributeSelector)                 extends Selector
  case class NotSelector(selector: Selector)                                                   extends Selector
  case class AndSelector(first: Selector, second: Selector)                                    extends Selector
  case class AfterSelector(previous: Selector, after: Selector)                                extends Selector
  case class ChildSelector(parent: Selector, child: Selector)                                  extends Selector
  case class InsideSelector(container: Selector, content: Selector)                            extends Selector

  def interpretTag(tag: String, flags: Set[FlagSelector], ignoreTag: Boolean = false): String =
    tag + (if (ignoreTag) "" else interpretFlags(flags))

  def interpretFlags(flags: Set[FlagSelector]): String = flags.map(interpret(_)).mkString

  def interpret(selector: Selector, ignoreTag: Boolean = false): String =
    selector match {
      case FlagSelector(flag)                               => s":$flag"
      case TagSelector(tag, flags)                          => interpretTag(tag, flags, ignoreTag)
      case AttributeSelectorAlone(attribute)                => s"[$attribute]"
      case AttributeSelectorValue(attribute, method, value) => s"[$attribute${method.symbol}=$value]"
      case ElementSelector(tagS, attributeS) =>
        interpret(tagS, true) + interpret(attributeS) + interpretFlags(tagS.flags)
      case NotSelector(selector)              => s":not(${interpret(selector)})"
      case AndSelector(first, second)         => interpret(first) + "," + interpret(second)
      case AfterSelector(previous, after)     => interpret(previous) + "+" + interpret(after)
      case ChildSelector(parent, child)       => interpret(parent) + ">" + interpret(child)
      case InsideSelector(container, content) => interpret(container) + " " + interpret(content)
    }

  def by(selector: Selector): By = By.cssSelector(interpret(selector))

  object Tag {
    val a      = TagSelector("a")
    val any    = TagSelector("*")
    val body   = TagSelector("body")
    val br     = TagSelector("br")
    val button = TagSelector("button")
    val canvas = TagSelector("canvas")
    val div    = TagSelector("div")
    val form   = TagSelector("form")
    val footer = TagSelector("footer")
    val h1     = TagSelector("h1")
    val h2     = TagSelector("h2")
    val h3     = TagSelector("h3")
    val h4     = TagSelector("h4")
    val h5     = TagSelector("h5")
    val h6     = TagSelector("h6")
    val head   = TagSelector("head")
    val header = TagSelector("header")
    val hr     = TagSelector("hr")
    val html   = TagSelector("html")
    val iframe = TagSelector("iframe")
    val img    = TagSelector("img")
    val input  = TagSelector("input")
    val li     = TagSelector("li")
    val link   = TagSelector("link")
    val meta   = TagSelector("meta")
    val nav    = TagSelector("nav")
    val ol     = TagSelector("ol")
    val p      = TagSelector("p")
    val table  = TagSelector("table")
    val title  = TagSelector("title")
    val root   = TagSelector(":root")
    val script = TagSelector("script")
    val span   = TagSelector("span")
    val ul     = TagSelector("ul")
  }

  object Attribute {
    val content = AttributeSelectorAlone("content")
    val href    = AttributeSelectorAlone("href")
    val id      = AttributeSelectorAlone("id")
    val klass   = AttributeSelectorAlone("class")
    val name    = AttributeSelectorAlone("name")
    val rel     = AttributeSelectorAlone("rel")
    val src     = AttributeSelectorAlone("src")
    val style   = AttributeSelectorAlone("style")
    val tipe    = AttributeSelectorAlone("type")
  }

  object Flag {
    val checked       = FlagSelector("checked")
    val disabled      = FlagSelector("disabled")
    val enabled       = FlagSelector("enabled")
    val required      = FlagSelector("required")
    val lang          = (lang: String) => FlagSelector(s"lang($lang)")
    val empty         = FlagSelector("empty")
    val firstChild    = FlagSelector("first-child")
    val firstOfType   = FlagSelector("first-of-type")
    val lastChild     = FlagSelector("last-child")
    val lastOfType    = FlagSelector("last-of-type")
    val nthChild      = (n: Int) => FlagSelector(s"nth-child($n)")
    val nthOfType     = (n: Int) => FlagSelector(s"nth-of-type($n)")
    val nthLastChild  = (n: Int) => FlagSelector(s"nth-last-child($n)")
    val nthLastOfType = (n: Int) => FlagSelector(s"nth-last-of-type($n)")
    val onlyOfType    = FlagSelector("only-of-type")
    val onlyChild     = FlagSelector("only-child")
  }
}
