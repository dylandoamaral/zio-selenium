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

  def interpretFlags(flags: Set[FlagSelector]): String = flags.map(flagS => s":${flagS.flag}").mkString

  def interpret(selector: Selector, ignoreTag: Boolean = false): String =
    selector match {
      case FlagSelector(flag)                               => s":$flag"
      case TagSelectorCommon(tag)                           => interpretTag(tag, Set.empty, ignoreTag)
      case TagSelectorLink(flags)                           => interpretTag("a", flags, ignoreTag)
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
    val div = TagSelectorCommon("div")
    val a   = TagSelectorLink()
  }

  object Attribute {
    val id = AttributeSelectorAlone("id")
  }

  object Flag {
    val visited   = FlagSelector("visited")
    val unvisited = FlagSelector("link")
    val hovered   = FlagSelector("hover")
    val active    = FlagSelector("active")
  }
}
