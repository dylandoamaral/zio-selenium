val zioVersion             = "1.0.9"
val seleniumVersion        = "3.141.59"
val htmlUnitDriverVersion  = "2.49.1"
val organizeImportsVersion = "0.5.0"

ThisBuild / scalaVersion := "2.13.3"
ThisBuild / scalacOptions += "-Wunused:imports"

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

ThisBuild / coverageExcludedPackages := ".*zinteract.example.*"

ThisBuild / githubWorkflowTargetTags ++= Seq("v*")
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("master")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowPublishPreamble +=
  WorkflowStep.Use(UseRef.Public("olafurpg", "setup-gpg", "v3"))

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

ThisBuild / githubWorkflowBuildPreamble := Seq(
  WorkflowStep.Run(
    name = Some("Create Webdriver folder"),
    commands = List(
      "mkdir ~/Webdriver"
    )
  ),
  WorkflowStep.Run(
    name = Some("Install chromedriver"),
    commands = List(
      "wget https://chromedriver.storage.googleapis.com/88.0.4324.96/chromedriver_linux64.zip",
      "unzip chromedriver*.zip -d ~/Webdriver"
    )
  ),
  WorkflowStep.Run(
    name = Some("Install geckodriver"),
    commands = List(
      "wget https://github.com/mozilla/geckodriver/releases/download/v0.28.0/geckodriver-v0.28.0-linux32.tar.gz",
      "tar zxvf geckodriver*.tar.gz -C ~/Webdriver"
    )
  ),
  WorkflowStep.Sbt(
    name = Some("Check formatting"),
    commands = List("scalafmtCheck")
  ),
  WorkflowStep.Sbt(
    name = Some("Check linting"),
    commands = List("scalafix --check")
  )
)
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("coverage", "test")))
ThisBuild / githubWorkflowBuildPostamble := Seq(
  WorkflowStep.Run(
    name = Some("Generate coverage"),
    commands = List("sbt coverageReport && bash <(curl -s https://codecov.io/bash)")
  )
)

ThisBuild / semanticdbEnabled := true
ThisBuild / semanticdbVersion := scalafixSemanticdb.revision
ThisBuild / scalafixDependencies += "com.github.liancheng" %% "organize-imports" % organizeImportsVersion

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
