val zioVersion            = "1.0.0-RC21-2"
val seleniumVersion       = "3.141.59"
val htmlUnitDriverVersion = "2.43.1"

ThisBuild / scalaVersion := "2.13.3"

ThisBuild / organization := "dev.doamaral"
ThisBuild / organizationName := "doamaral"
ThisBuild / organizationHomepage := Some(url("https://www.dylan.doamaral.dev/"))

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/dylandoamaral/zinteract"),
    "scm:git@github.com:dylandoamaral/zinteract.git"
  )
)
ThisBuild / developers := List(
  Developer(
    id = "ddoamaral",
    name = "Dylan DO AMARAL",
    email = "do.amaral.dylan@gmail.com",
    url = url("https://www.dylan.doamaral.dev/")
  )
)

ThisBuild / description := "A ZIO wrapper to interact with a browser using Selenium."
ThisBuild / licenses := List("Apache 2" -> new URL("http://www.apache.org/licenses/LICENSE-2.0.txt"))
ThisBuild / homepage := Some(url("https://github.com/dylandoamaral/zinteract"))

ThisBuild / pomIncludeRepository := { _ => false }

ThisBuild / coverageExcludedPackages := ".*zinteract.example.*"

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches ++=
  Seq(RefPredicate.StartsWith(Ref.Tag("v")))

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use("olafurpg", "setup-gpg", "v2")

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE"    -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET"        -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}"
    )
  )
)

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
