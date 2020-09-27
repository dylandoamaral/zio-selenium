# Zinteract

## Description

A ZIO friendly librairy to interact with a browser using Selenium.

## Why use it

- You need to test website interactions
- You want to create browser automation

## How it works

Here is a sample to link to a particular website:

```scala
import zio.{App, ExitCode, ZIO}

import zinteract.webdriver.{Property, WebDriver}
import zinteract.surfer.Surfer

object Zinteract extends App {
  val app = for {
    _ <- surfer.link("https://www.selenium.dev/documentation/en/")
  } yield ()

  val pathToDriver = "/path/to/webdriver/chromedriver"

  override def run(args: List[String]): zio.URIO[zio.ZEnv, ExitCode] =
    app
      .provideCustomLayer(
        WebDriver.Service.chromeMinConfig(pathToDriver) >>> Surfer.Service.live
      )
      .exitCode
}
```

## Is it usable ?

Not for the moment, feel free to contribute or raise issues !
