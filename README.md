<p align="center">
  <img src="https://raw.githubusercontent.com/dylandoamaral/zinteract/master/images/zinteract.png" />
</p>

<h1 align="center">Zinteract</h1>

<p align="center">
  <a href="https://github.com/dylandoamaral/zinteract/actions">
    <img src="https://github.com/dylandoamaral/zinteract/workflows/Continuous%20Integration/badge.svg" />
  </a>
  <a href="https://codecov.io/gh/dylandoamaral/zinteract">
    <img src="https://codecov.io/gh/dylandoamaral/zinteract/branch/master/graph/badge.svg" />
  </a>
  <a href="https://scala-steward.org">
    <img src="https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=" />
  </a>
</p>

<p align="center">A ZIO friendly library to interact with a browser using Selenium.</p>

## Why use it 🤔

For <img src="https://raw.githubusercontent.com/dylandoamaral/zinteract/master/images/zio.png" width=32 height=16/> users:
- You need to test website interactions
- You want to create browser automation

For <img src="https://raw.githubusercontent.com/dylandoamaral/zinteract/master/images/selenium.png" width=16 height=16/> users:
 - You need to run selenium in parallel
 - You want to retry elegantly using zio schedule
 - You want to use selenium purely

## Using Zinteract 🏢

The latest version is 0.2.0, which is avaible for scala 2.13.

If you're using sbt, add the following to your build:

```bash
libraryDependencies ++= Seq(
  "dev.doamaral" %% "zinteract" % "0.2.0"
)
```

## How it works 🔧

Here is a sample to link to a particular website and retrieve the title:

```scala
import zio._

import zinteract.webdriver
import zinteract.builder.chrome
import zinteract.builder.ChromeBlueprint.default

import org.openqa.selenium.By

object FindElement extends ZIOAppDefault {
  val app = for {
    _       <- webdriver.link("https://www.selenium.dev/documentation/en/")
    element <- webdriver.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- Console.printLine(s"Title: ${element.getText}")
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}

```

## Community 🤝

If you have a problem using zinteract or if you want to add features, issues and pull requests are welcome.

Don't hesitate to give a ⭐ to the project if you like it!
