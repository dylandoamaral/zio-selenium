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
    suite("Tag Selector Spec")(
      test("A is a tag selector") {
        assert(interpret(a))(equalTo("a"))
      },
      test("Body is a tag selector") {
        assert(interpret(body))(equalTo("body"))
      },
      test("Br is a tag selector") {
        assert(interpret(br))(equalTo("br"))
      },
      test("Button is a tag selector") {
        assert(interpret(button))(equalTo("button"))
      },
      test("Canvas is a tag selector") {
        assert(interpret(canvas))(equalTo("canvas"))
      },
      test("Caption is a tag selector") {
        assert(interpret(caption))(equalTo("caption"))
      },
      test("Div is a tag selector") {
        assert(interpret(div))(equalTo("div"))
      },
      test("Form is a tag selector") {
        assert(interpret(form))(equalTo("form"))
      },
      test("Footer is a tag selector") {
        assert(interpret(footer))(equalTo("footer"))
      },
      test("Fullscreen is a tag selector") {
        assert(interpret(fullscreen))(equalTo(":fullscreen"))
      },
      test("H1 is a tag selector") {
        assert(interpret(h1))(equalTo("h1"))
      },
      test("H2 is a tag selector") {
        assert(interpret(h2))(equalTo("h2"))
      },
      test("H3 is a tag selector") {
        assert(interpret(h3))(equalTo("h3"))
      },
      test("H4 is a tag selector") {
        assert(interpret(h4))(equalTo("h4"))
      },
      test("H5 is a tag selector") {
        assert(interpret(h5))(equalTo("h5"))
      },
      test("H6 is a tag selector") {
        assert(interpret(h6))(equalTo("h6"))
      },
      test("Head is a tag selector") {
        assert(interpret(head))(equalTo("head"))
      },
      test("Header is a tag selector") {
        assert(interpret(header))(equalTo("header"))
      },
      test("Hr is a tag selector") {
        assert(interpret(hr))(equalTo("hr"))
      },
      test("Html is a tag selector") {
        assert(interpret(html))(equalTo("html"))
      },
      test("Iframe is a tag selector") {
        assert(interpret(iframe))(equalTo("iframe"))
      },
      test("Img is a tag selector") {
        assert(interpret(img))(equalTo("img"))
      },
      test("Input is a tag selector") {
        assert(interpret(input))(equalTo("input"))
      },
      test("Li is a tag selector") {
        assert(interpret(li))(equalTo("li"))
      },
      test("Link is a tag selector") {
        assert(interpret(link))(equalTo("link"))
      },
      test("Meta is a tag selector") {
        assert(interpret(meta))(equalTo("meta"))
      },
      test("Nav is a tag selector") {
        assert(interpret(nav))(equalTo("nav"))
      },
      test("Ol is a tag selector") {
        assert(interpret(ol))(equalTo("ol"))
      },
      test("P is a tag selector") {
        assert(interpret(p))(equalTo("p"))
      },
      test("Table is a tag selector") {
        assert(interpret(table))(equalTo("table"))
      },
      test("Title is a tag selector") {
        assert(interpret(title))(equalTo("title"))
      },
      test("Root is a tag selector") {
        assert(interpret(root))(equalTo(":root"))
      },
      test("Script is a tag selector") {
        assert(interpret(script))(equalTo("script"))
      },
      test("Span is a tag selector") {
        assert(interpret(span))(equalTo("span"))
      },
      test("Ul is a tag selector") {
        assert(interpret(ul))(equalTo("ul"))
      }
    )

  def suiteAttributeSelector =
    suite("Attribute Selector Spec")(
      test("Class is an attribute selector") {
        assert(interpret(Class))(equalTo("[class]"))
      },
      test("Content is an attribute selector") {
        assert(interpret(content))(equalTo("[content]"))
      },
      test("Href is an attribute selector") {
        assert(interpret(href))(equalTo("[href]"))
      },
      test("Id is an attribute selector") {
        assert(interpret(id))(equalTo("[id]"))
      },
      test("Name is an attribute selector") {
        assert(interpret(name))(equalTo("[name]"))
      },
      test("Rel is an attribute selector") {
        assert(interpret(rel))(equalTo("[rel]"))
      },
      test("Src is an attribute selector") {
        assert(interpret(src))(equalTo("[src]"))
      },
      test("Style is an attribute selector") {
        assert(interpret(style))(equalTo("[style]"))
      },
      test("Type is an attribute selector") {
        assert(interpret(Type))(equalTo("[type]"))
      }
    )

  def suiteFlagSelector =
    suite("Flag Selector Spec")(
      test("Visited is a flag selector") {
        assert(interpret(visited))(equalTo(":visited"))
      },
      test("Unvisited is a flag selector") {
        assert(interpret(unvisited))(equalTo(":link"))
      },
      test("Hovered is a flag selector") {
        assert(interpret(hovered))(equalTo(":hover"))
      },
      test("Active is a flag selector") {
        assert(interpret(active))(equalTo(":active"))
      },
      test("Checked is a flag selector") {
        assert(interpret(checked))(equalTo(":checked"))
      },
      test("Default is a flag selector") {
        assert(interpret(default))(equalTo(":default"))
      },
      test("Disabled is a flag selector") {
        assert(interpret(disabled))(equalTo(":disabled"))
      },
      test("Enabled is a flag selector") {
        assert(interpret(enabled))(equalTo(":enabled"))
      },
      test("Focused is a flag selector") {
        assert(interpret(focused))(equalTo(":focus"))
      },
      test("Inrange is a flag selector") {
        assert(interpret(inRange))(equalTo(":in-range"))
      },
      test("Indeterminate is a flag selector") {
        assert(interpret(indeterminate))(equalTo(":indeterminate"))
      },
      test("Invalid is a flag selector") {
        assert(interpret(invalid))(equalTo(":invalid"))
      },
      test("Optimal is a flag selector") {
        assert(interpret(optimal))(equalTo(":optimal"))
      },
      test("Outofrange is a flag selector") {
        assert(interpret(outOfRange))(equalTo(":out-of-range"))
      },
      test("Placeholder is a flag selector") {
        assert(interpret(placeholder))(equalTo("::placeholder"))
      },
      test("Readonly is a flag selector") {
        assert(interpret(readOnly))(equalTo(":read-only"))
      },
      test("Notreadwrite is a flag selector") {
        assert(interpret(notReadWrite))(equalTo(":read-write"))
      },
      test("Required is a flag selector") {
        assert(interpret(required))(equalTo(":required"))
      },
      test("Valid is a flag selector") {
        assert(interpret(valid))(equalTo(":valid"))
      },
      test("Lang is a flag selector") {
        assert(interpret(lang("it")))(equalTo(":lang(it)"))
      },
      test("Empty is a flag selector") {
        assert(interpret(empty))(equalTo(":empty"))
      },
      test("Firstchild is a flag selector") {
        assert(interpret(firstChild))(equalTo(":first-child"))
      },
      test("Firstoftype is a flag selector") {
        assert(interpret(firstOfType))(equalTo(":first-of-type"))
      },
      test("Lastchild is a flag selector") {
        assert(interpret(lastChild))(equalTo(":last-child"))
      },
      test("Lastoftype is a flag selector") {
        assert(interpret(lastOfType))(equalTo(":last-of-type"))
      },
      test("Nthchild is a flag selector") {
        assert(interpret(nthChild(2)))(equalTo(":nth-child(2)"))
      },
      test("Nthlastchild is a flag selector") {
        assert(interpret(nthLastChild(2)))(equalTo(":nth-last-child(2)"))
      },
      test("Nthlastoftype is a flag selector") {
        assert(interpret(nthLastOfType(2)))(equalTo(":nth-last-of-type(2)"))
      },
      test("Nthoftype is a flag selector") {
        assert(interpret(nthOfType(2)))(equalTo(":nth-of-type(2)"))
      },
      test("Onlyoftype is a flag selector") {
        assert(interpret(onlyOfType))(equalTo(":only-of-type"))
      },
      test("Onlychild is a flag selector") {
        assert(interpret(onlyChild))(equalTo(":only-child"))
      }
    )

  def spec = suite("Selector Spec")(suiteSelector, suiteTagSelector, suiteAttributeSelector, suiteFlagSelector)
}
