# Zinteract

[![Actions Status](https://github.com/dylandoamaral/zinteract/workflows/Continuous%20Integration/badge.svg)](https://github.com/dylandoamaral/zinteract/actions)
[![codecov](https://codecov.io/gh/dylandoamaral/zinteract/branch/master/graph/badge.svg)](https://codecov.io/gh/dylandoamaral/zinteract)

A ZIO friendly library to interact with a browser using Selenium.

## Why use it

- You need to test website interactions
- You want to create browser automation

## Using Zinteract

The latest version is 0.1.0, which is avaible for scala 2.13.

If you're using sbt, add the following to your build:

```bash
libraryDependencies ++= Seq(
  "dev.doamaral" %% "zinteract" % "0.1.0"
)
```

## How it works

Here is a sample to link to a particular website and retrieve the title:

```scala
import zio.{App, ExitCode}
import zio.console

import zinteract.session
import zinteract.webdriver.ChromeBlueprintOps.default
import zinteract.webdriver.BuilderOps.chrome

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- session.link("https://www.selenium.dev/documentation/en/")
    element <- session.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val builder = chrome at "/path/to/webdriver/chromedriver" using default

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(builder.buildLayer)
      .exitCode
}

```
