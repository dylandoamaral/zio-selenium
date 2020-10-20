# Zinteract

[![Actions Status](https://github.com/dylandoamaral/zinteract/workflows/Scala%20CI/badge.svg)](https://github.com/dylandoamaral/zinteract/actions)
[![codecov](https://codecov.io/gh/dylandoamaral/zinteract/branch/master/graph/badge.svg)](https://codecov.io/gh/dylandoamaral/zinteract)

A ZIO friendly librairy to interact with a browser using Selenium.

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

Here is a sample to link to a particular website and retrive the title:

```scala
import zio.{App, ExitCode, ZIO}
import zio.console

import zinteract.webdriver.WebDriver
import zinteract.session

import org.openqa.selenium.By

object FindElement extends App {
  val app = for {
    _       <- session.link("https://www.selenium.dev/documentation/en/")
    element <- session.findElement(By.id("the-selenium-browser-automation-project"))
    _       <- console.putStrLn(s"Title: ${element.getText()}")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> session.Session.Service.live
      )
      .exitCode
}
```

## Is it usable ?

Not for the moment, feel free to contribute or raise issues !
