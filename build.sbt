val zioVersion            = "1.0.0-RC21-2"
val seleniumVersion       = "3.141.59"
val htmlUnitDriverVersion = "2.43.1"

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / organization := "me.dylandoamaral"
ThisBuild / organizationName := "dylandoamaral"
ThisBuild / coverageExcludedPackages := ".*zinteract.examples.*"

lazy val root = (project in file("."))
  .settings(
    name := "zinteract",
    libraryDependencies ++= Seq(
      "dev.zio"                %% "zio"             % zioVersion,
      "dev.zio"                %% "zio-test"        % zioVersion % "test",
      "dev.zio"                %% "zio-test-sbt"    % zioVersion % "test",
      "org.seleniumhq.selenium" % "selenium-java"   % seleniumVersion,
      "org.seleniumhq.selenium" % "htmlunit-driver" % htmlUnitDriverVersion
    ),
    testFrameworks += new TestFramework("zio.test.sbt.ZTestFramework")
  )
