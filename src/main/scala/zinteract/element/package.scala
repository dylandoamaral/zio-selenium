package zinteract

import zio.{RIO, Task, ZIO}
import zio.clock.Clock

import zinteract.context._

import org.openqa.selenium.{By, Dimension, Keys, NoSuchElementException, Point, Rectangle, WebElement}

/**
  * Element provides a way to interact purely with webelement
  *
  * {{{
  * val effect = for {
  *    _          <- session.link("https://www.selenium.dev/documentation/en/")
  *    search     <- session.findElement(By.cssSelector("[type=search]"))
  *    _          <- search.sendKeysM("Introduction")
  * } yield ()
  *
  * app.provideCustomLayer(ChromeBuilder(pathToDriver).buildLayer >>> session.Session.Service.live)
  * }}}
  */
package object element {

  /**
    * Provides implicit definition for fonction of element that can
    * be called directly from a webelement.
    */
  implicit class ZinteractWebElement(element: WebElement) {

    /**
      * Click this element. If this causes a new page to load, you
      * should discard all references to this element and any further
      * operations performed on this element will throw a
      * StaleElementReferenceException.
      *
      * Note that if click() is done by sending a native event (which is
      * the default on most browsers/platforms) then the method will
      * _not_ wait for the next page to load and the caller should verify
      * that themselves.
      *
      * There are some preconditions for an element to be clicked. The
      * element must be visible and it must have a height and width
      * greater then 0.
      */
    val clickM: Task[Unit] =
      clickOn(element)

    /**
      * If this element is a text entry element, this will clear the value. Has no effect on other
      * elements. Text entry elements are INPUT and TEXTAREA elements.
      *
      * Note that the events fired by this event may not be as you'd expect. In particular, we don't
      * fire any keyboard or mouse events.
      */
    val clearM: Task[Unit] =
      clearOn(element)

    /**
      * Use this method to simulate typing into an element, which may set its value.
      */
    def sendKeysM(text: CharSequence): Task[Unit] =
      sendKeysOn(element)(text)

    /**
      * Use this method to simulate the key enter into an element.
      */
    val pressEnterM: Task[Unit] =
      pressEnterOn(element)

    /**
      * If this current element is a form, or an element within a form, then this will be submitted to
      * the remote server. If this causes the current page to change, then this method will block until
      * the new page is loaded.
      */
    val submitM: Task[Unit] =
      submitOn(element)

    /**
      * Gets the value of the given attribute of the element. Will return the current value, even if
      * this has been modified after the page has been loaded.
      *
      * <p>More exactly, this method will return the value of the property with the given name, if it
      * exists. If it does not, then the value of the attribute with the given name is returned. If
      * neither exists, null is returned.
      *
      * <p>The "style" attribute is converted as best can be to a text representation with a trailing
      * semi-colon.
      *
      * <p>The following are deemed to be "boolean" attributes, and will return either "true" or null:
      *
      * <p>async, autofocus, autoplay, checked, compact, complete, controls, declare, defaultchecked,
      * defaultselected, defer, disabled, draggable, ended, formnovalidate, hidden, indeterminate,
      * iscontenteditable, ismap, itemscope, loop, multiple, muted, nohref, noresize, noshade,
      * novalidate, nowrap, open, paused, pubdate, readonly, required, reversed, scoped, seamless,
      * seeking, selected, truespeed, willvalidate
      *
      * <p>Finally, the following commonly mis-capitalized attribute/property names are evaluated as
      * expected:
      *
      * <ul>
      * <li>If the given name is "class", the "className" property is returned.
      * <li>If the given name is "readonly", the "readOnly" property is returned.
      * </ul>
      *
      * <i>Note:</i> The reason for this behavior is that users frequently confuse attributes and
      * properties. If you need to do something more precise, e.g., refer to an attribute even when a
      * property of the same name exists, then you should evaluate Javascript to obtain the result
      * you desire.
      */
    def getAttributeM(name: String): Task[String] =
      getAttributeOf(element)(name)

    /**
      * Get the visible (i.e. not hidden by CSS) text of this element, including sub-elements.
      */
    val getTextM: Task[String] =
      getTextOf(element)

    /**
      * Gets the value of a given CSS property.
      * Color values should be returned as rgba strings, so,
      * for example if the "background-color" property is set as "green" in the
      * HTML source, the returned value will be "rgba(0, 255, 0, 1)".
      *
      * Note that shorthand CSS properties (e.g. background, font, border, border-top, margin,
      * margin-top, padding, padding-top, list-style, outline, pause, cue) are not returned,
      * in accordance with the
      * [[http://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSStyleDeclaration DOM CSS2 specification]]
      * - you should directly access the longhand properties (e.g. background-color) to access the
      * desired values.
      */
    def getCssValueM(propertyName: String): Task[String] =
      getCssValueOf(element)(propertyName)

    /**
      * Gets the tag name of this element. <b>Not</b> the value of the name attribute: will return
      * <code>"input"</code> for the element <code>&lt;input name="foo" /&gt;</code>.
      */
    val getTagNameM: Task[String] =
      getTagNameOf(element)

    /**
      * Where on the page is the top left-hand corner of the rendered element.
      */
    val getLocationM: Task[Point] =
      getLocationOf(element)

    /**
      * Returns the location and size of the rendered element.
      */
    val getRectM: Task[Rectangle] =
      getRectOf(element)

    /**
      * Returns The size of the element on the page.
      */
    val getSizeM: Task[Dimension] =
      getSizeOf(element)

    /**
      * Returns if this element displayed or not. This method avoids the problem of having to
      * parse an element's "style" attribute.
      */
    val isDisplayedM: Task[Boolean] =
      isDisplayed(element)

    /**
      * Returns if the element currently enabled or not. This will generally return true for everything but
      * disabled input elements.
      *
      * @return True if the element is enabled, false otherwise.
      */
    val isEnabledM: Task[Boolean] =
      isEnabled(element)

    /**
      * Determines whether or not this element is selected or not. This operation only applies to input
      * elements such as checkboxes, options in a select and radio buttons.
      * For more information on which elements this method supports,
      * refer to the <a href="https://w3c.github.io/webdriver/webdriver-spec.html#is-element-selected">specification</a>.
      */
    val isSelectedM: Task[Boolean] =
      isSelected(element)

    /**
      * Finds the first WebElement using the given method.
      */
    def findElementM(by: By)(implicit wait: WaitConfig = None): ZIO[Clock, NoSuchElementException, WebElement] =
      findElementFrom(element)(by)(wait)

    /**
      * Finds all WebElements using the given method.
      */
    def findElementsM(by: By)(implicit wait: WaitConfig = None): RIO[Clock, List[WebElement]] =
      findElementsFrom(element)(by)(wait)

    /**
      * Checks if the given method find an element.
      */
    def hasElementM(by: By)(implicit wait: WaitConfig = None): RIO[Clock, Boolean] =
      hasElementFrom(element)(by)(wait)
  }

  /**
    * Click this element. If this causes a new page to load, you
    * should discard all references to this element and any further
    * operations performed on this element will throw a
    * StaleElementReferenceException.
    *
    * Note that if click() is done by sending a native event (which is
    * the default on most browsers/platforms) then the method will
    * _not_ wait for the next page to load and the caller should verify
    * that themselves.
    *
    * There are some preconditions for an element to be clicked. The
    * element must be visible and it must have a height and width
    * greater then 0.
    */
  def clickOn(element: WebElement): Task[Unit] =
    ZIO.effect(element.click)

  /**
    * If this element is a text entry element, this will clear the value. Has no effect on other
    * elements. Text entry elements are INPUT and TEXTAREA elements.
    *
    * Note that the events fired by this event may not be as you'd expect. In particular, we don't
    * fire any keyboard or mouse events.
    */
  def clearOn(element: WebElement): Task[Unit] =
    ZIO.effect(element.clear)

  /**
    * Simulates typing into an element, which may set its value.
    */
  def sendKeysOn(element: WebElement)(text: CharSequence): Task[Unit] =
    ZIO.effect(element.sendKeys(text))

  /**
    * Simulates the key enter into an element.
    */
  def pressEnterOn(element: WebElement): Task[Unit] =
    sendKeysOn(element)(Keys.ENTER)

  /**
    * If this current element is a form, or an element within a form, then this will be submitted to
    * the remote server. If this causes the current page to change, then this method will block until
    * the new page is loaded.
    */
  def submitOn(element: WebElement): Task[Unit] =
    ZIO.effect(element.submit)

  /**
    * Gets the value of the given attribute of the element. Will return the current value, even if
    * this has been modified after the page has been loaded.
    *
    * <p>More exactly, this method will return the value of the property with the given name, if it
    * exists. If it does not, then the value of the attribute with the given name is returned. If
    * neither exists, null is returned.
    *
    * <p>The "style" attribute is converted as best can be to a text representation with a trailing
    * semi-colon.
    *
    * <p>The following are deemed to be "boolean" attributes, and will return either "true" or null:
    *
    * <p>async, autofocus, autoplay, checked, compact, complete, controls, declare, defaultchecked,
    * defaultselected, defer, disabled, draggable, ended, formnovalidate, hidden, indeterminate,
    * iscontenteditable, ismap, itemscope, loop, multiple, muted, nohref, noresize, noshade,
    * novalidate, nowrap, open, paused, pubdate, readonly, required, reversed, scoped, seamless,
    * seeking, selected, truespeed, willvalidate
    *
    * <p>Finally, the following commonly mis-capitalized attribute/property names are evaluated as
    * expected:
    *
    * <ul>
    * <li>If the given name is "class", the "className" property is returned.
    * <li>If the given name is "readonly", the "readOnly" property is returned.
    * </ul>
    *
    * <i>Note:</i> The reason for this behavior is that users frequently confuse attributes and
    * properties. If you need to do something more precise, e.g., refer to an attribute even when a
    * property of the same name exists, then you should evaluate Javascript to obtain the result
    * you desire.
    */
  def getAttributeOf(element: WebElement)(name: String): Task[String] =
    ZIO.effect(element.getAttribute(name))

  /**
    * Gets the visible (i.e. not hidden by CSS) text of this element, including sub-elements.
    */
  def getTextOf(element: WebElement): Task[String] =
    ZIO.effect(element.getText)

  /**
    * Gets the value of a given CSS property.
    * Color values should be returned as rgba strings, so,
    * for example if the "background-color" property is set as "green" in the
    * HTML source, the returned value will be "rgba(0, 255, 0, 1)".
    *
    * Note that shorthand CSS properties (e.g. background, font, border, border-top, margin,
    * margin-top, padding, padding-top, list-style, outline, pause, cue) are not returned,
    * in accordance with the
    * [[http://www.w3.org/TR/DOM-Level-2-Style/css.html#CSS-CSSStyleDeclaration DOM CSS2 specification]]
    * - you should directly access the longhand properties (e.g. background-color) to access the
    * desired values.
    */
  def getCssValueOf(element: WebElement)(propertyName: String): Task[String] =
    ZIO.effect(element.getCssValue(propertyName))

  /**
    * Gets the tag name of this element. <b>Not</b> the value of the name attribute: will return
    * <code>"input"</code> for the element <code>&lt;input name="foo" /&gt;</code>.
    */
  def getTagNameOf(element: WebElement): Task[String] =
    ZIO.effect(element.getTagName)

  /**
    * Where on the page is the top left-hand corner of the rendered element.
    */
  def getLocationOf(element: WebElement): Task[Point] =
    ZIO.effect(element.getLocation)

  /**
    * Returns the location and size of the rendered element.
    */
  def getRectOf(element: WebElement): Task[Rectangle] =
    ZIO.effect(element.getRect)

  /**
    * Returns The size of the element on the page.
    */
  def getSizeOf(element: WebElement): Task[Dimension] =
    ZIO.effect(element.getSize)

  /**
    * Returns if this element displayed or not. This method avoids the problem of having to
    * parse an element's "style" attribute.
    */
  def isDisplayed(element: WebElement): Task[Boolean] =
    ZIO.effect(element.isDisplayed)

  /**
    * Returns if the element currently enabled or not. This will generally return true for everything but
    * disabled input elements.
    *
    * @return True if the element is enabled, false otherwise.
    */
  def isEnabled(element: WebElement): Task[Boolean] =
    ZIO.effect(element.isEnabled)

  /**
    * Determines whether or not this element is selected or not. This operation only applies to input
    * elements such as checkboxes, options in a select and radio buttons.
    * For more information on which elements this method supports,
    * refer to the <a href="https://w3c.github.io/webdriver/webdriver-spec.html#is-element-selected">specification</a>.
    */
  def isSelected(element: WebElement): Task[Boolean] =
    ZIO.effect(element.isSelected)
}
