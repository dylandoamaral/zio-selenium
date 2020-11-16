# Zinteract

[![Actions Status](https://github.com/dylandoamaral/zinteract/workflows/Continuous%20Integration/badge.svg)](https://github.com/dylandoamaral/zinteract/actions)
[![codecov](https://codecov.io/gh/dylandoamaral/zinteract/branch/master/graph/badge.svg)](https://codecov.io/gh/dylandoamaral/zinteract)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

A ZIO friendly library to interact with a browser using Selenium.

## Why use it

- You need to test website interactions
- You want to create browser automation

## Using Zinteract

The latest version is 0.1.1, which is avaible for scala 2.13.

If you're using sbt, add the following to your build:

```bash
libraryDependencies ++= Seq(
  "dev.doamaral" %% "zinteract" % "0.1.1"
)
```

## How it works

Here is a sample to link to a particular website and retrieve the title:

```scala
import zio.{App, ExitCode}
import zio.console

import zinteract.webdriver
import zinteract.builder.chrome
import zinteract.builder.ChromeBlueprint.default

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- webdriver.link("https://www.selenium.dev/documentation/en/")
    element <- webdriver.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val builder = chrome at "/path/to/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}

```
