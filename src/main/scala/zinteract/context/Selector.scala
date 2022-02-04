package zinteract.context

import org.openqa.selenium.By

/** Selector is a DSL to create css selector in an english way.
  *
  * Examples:
  *   - href contains "test" in a
  *   - id equalsTo "my-id" in p
  *   - h1 and h2
  *   - id.not
  */
object Selector {
  sealed trait SearchingMethod { def symbol: String }
  case object Equal     extends SearchingMethod { def symbol: String = ""  }
  case object Contains  extends SearchingMethod { def symbol: String = "*" }
  case object StartWith extends SearchingMethod { def symbol: String = "^" }
  case object EndWith   extends SearchingMethod { def symbol: String = "$" }

  sealed trait Selector {
    def and(that: Selector): AndSelector         = AndSelector(that, this)
    def after(that: Selector): AfterSelector     = AfterSelector(that, this)
    def inside(that: Selector): InsideSelector   = InsideSelector(that, this)
    def childOf(parent: Selector): ChildSelector = ChildSelector(parent, this)
    def not: NotSelector                         = NotSelector(this)
  }
  sealed trait AttributeSelector extends Selector {
    def in(tagS: TagSelector): ElementSelector = ElementSelector(tagS, this)
  }

  case class FlagSelector(flag: String) extends Selector
  case class TagSelector(tag: String, flags: Set[FlagSelector] = Set.empty) extends Selector {
    def being(flagS: FlagSelector): TagSelector                    = TagSelector(tag, flags + flagS)
    def containing(attributeS: AttributeSelector): ElementSelector = ElementSelector(this, attributeS)
  }
  case class AttributeSelectorAlone(attribute: String) extends AttributeSelector {
    def equalsTo(value: String): AttributeSelectorValue   = AttributeSelectorValue(attribute, Equal, value)
    def contains(value: String): AttributeSelectorValue   = AttributeSelectorValue(attribute, Contains, value)
    def startsWith(value: String): AttributeSelectorValue = AttributeSelectorValue(attribute, StartWith, value)
    def endsWith(value: String): AttributeSelectorValue   = AttributeSelectorValue(attribute, EndWith, value)
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

  /** Interpret a Selector structure into a css selector statement
    */
  def interpret(selector: Selector, ignoreTag: Boolean = false): String =
    selector match {
      case FlagSelector(flag)                               => s":$flag"
      case TagSelector(tag, flags)                          => interpretTag(tag, flags, ignoreTag)
      case AttributeSelectorAlone(attribute)                => s"[$attribute]"
      case AttributeSelectorValue(attribute, method, value) => s"[$attribute${method.symbol}='$value']"
      case ElementSelector(tagS, attributeS) =>
        interpret(tagS, ignoreTag = true) + interpret(attributeS) + interpretFlags(tagS.flags)
      case NotSelector(selector)              => s":not(${interpret(selector)})"
      case AndSelector(first, second)         => interpret(first) + "," + interpret(second)
      case AfterSelector(previous, after)     => interpret(previous) + "+" + interpret(after)
      case ChildSelector(parent, child)       => interpret(parent) + ">" + interpret(child)
      case InsideSelector(container, content) => interpret(container) + " " + interpret(content)
    }

  /** Interpret a Selector structure into a By css selector
    */
  def by(selector: Selector): By = By.cssSelector(interpret(selector))

  val a: TagSelector      = TagSelector("a")
  val any: TagSelector    = TagSelector("*")
  val body: TagSelector   = TagSelector("body")
  val br: TagSelector     = TagSelector("br")
  val button: TagSelector = TagSelector("button")
  val canvas: TagSelector = TagSelector("canvas")
  val div: TagSelector    = TagSelector("div")
  val form: TagSelector   = TagSelector("form")
  val footer: TagSelector = TagSelector("footer")
  val h1: TagSelector     = TagSelector("h1")
  val h2: TagSelector     = TagSelector("h2")
  val h3: TagSelector     = TagSelector("h3")
  val h4: TagSelector     = TagSelector("h4")
  val h5: TagSelector     = TagSelector("h5")
  val h6: TagSelector     = TagSelector("h6")
  val head: TagSelector   = TagSelector("head")
  val header: TagSelector = TagSelector("header")
  val hr: TagSelector     = TagSelector("hr")
  val html: TagSelector   = TagSelector("html")
  val iframe: TagSelector = TagSelector("iframe")
  val img: TagSelector    = TagSelector("img")
  val input: TagSelector  = TagSelector("input")
  val li: TagSelector     = TagSelector("li")
  val link: TagSelector   = TagSelector("link")
  val meta: TagSelector   = TagSelector("meta")
  val nav: TagSelector    = TagSelector("nav")
  val ol: TagSelector     = TagSelector("ol")
  val p: TagSelector      = TagSelector("p")
  val table: TagSelector  = TagSelector("table")
  val title: TagSelector  = TagSelector("title")
  val root: TagSelector   = TagSelector(":root")
  val script: TagSelector = TagSelector("script")
  val span: TagSelector   = TagSelector("span")
  val ul: TagSelector     = TagSelector("ul")

  val content: AttributeSelectorAlone = AttributeSelectorAlone("content")
  val href: AttributeSelectorAlone    = AttributeSelectorAlone("href")
  val id: AttributeSelectorAlone      = AttributeSelectorAlone("id")
  val klass: AttributeSelectorAlone   = AttributeSelectorAlone("class")
  val name: AttributeSelectorAlone    = AttributeSelectorAlone("name")
  val rel: AttributeSelectorAlone     = AttributeSelectorAlone("rel")
  val src: AttributeSelectorAlone     = AttributeSelectorAlone("src")
  val style: AttributeSelectorAlone   = AttributeSelectorAlone("style")
  val tipe: AttributeSelectorAlone    = AttributeSelectorAlone("type")

  val checked: FlagSelector              = FlagSelector("checked")
  val disabled: FlagSelector             = FlagSelector("disabled")
  val enabled: FlagSelector              = FlagSelector("enabled")
  val required: FlagSelector             = FlagSelector("required")
  val lang: String => FlagSelector       = (lang: String) => FlagSelector(s"lang($lang)")
  val empty: FlagSelector                = FlagSelector("empty")
  val firstChild: FlagSelector           = FlagSelector("first-child")
  val firstOfType: FlagSelector          = FlagSelector("first-of-type")
  val lastChild: FlagSelector            = FlagSelector("last-child")
  val lastOfType: FlagSelector           = FlagSelector("last-of-type")
  val nthChild: Int => FlagSelector      = (n: Int) => FlagSelector(s"nth-child($n)")
  val nthOfType: Int => FlagSelector     = (n: Int) => FlagSelector(s"nth-of-type($n)")
  val nthLastChild: Int => FlagSelector  = (n: Int) => FlagSelector(s"nth-last-child($n)")
  val nthLastOfType: Int => FlagSelector = (n: Int) => FlagSelector(s"nth-last-of-type($n)")
  val onlyOfType: FlagSelector           = FlagSelector("only-of-type")
  val onlyChild: FlagSelector            = FlagSelector("only-child")
}
