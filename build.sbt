val zioVersion            = "2.0.2"
val seleniumVersion       = "4.6.0"
val htmlUnitDriverVersion = "4.6.0"

ThisBuild / scalaVersion := "2.13.8"
ThisBuild / scalacOptions += "-Wunused:imports"

ThisBuild / organization         := "dev.doamaral"
ThisBuild / organizationName     := "doamaral"
ThisBuild / organizationHomepage := Some(url("https://github.com/dylandoamaral"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/dylandoamaral/zio-selenium"),
    "scm:git@github.com:dylandoamaral/zio-selenium.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id    = "ddoamaral",
    name  = "Dylan DO AMARAL",
    email = "do.amaral.dylan@gmail.com",
    url   = url("https://www.dylan.doamaral.dev/")
  )
)

ThisBuild / description := "A ZIO wrapper to interact with a browser using Selenium."
ThisBuild / licenses := List("Apache 2" -> new URL("https://github.com/dylandoamaral/zio-selenium/blob/master/LICENSE"))
ThisBuild / homepage := Some(url("https://github.com/dylandoamaral/zio-selenium"))

ThisBuild / coverageExcludedPackages := ".*zio.selenium.example.*"

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision

lazy val root =
  (project in file("."))
    .settings(
      name := "zio-selenium",
      libraryDependencies ++= Seq(
        "dev.zio"                %% "zio"             % zioVersion,
        "dev.zio"                %% "zio-test"        % zioVersion % "test",
        "dev.zio"                %% "zio-test-sbt"    % zioVersion % "test",
        "org.seleniumhq.selenium" % "selenium-java"   % seleniumVersion,
        "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitDriverVersion
      ),
      testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework"),
      Test / parallelExecution := false
    )

addCommandAlias("check", "; scalafmtCheckAll; scalafixAll --check;")
