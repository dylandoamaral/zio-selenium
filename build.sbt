val zioVersion            = "2.0.7"
val seleniumVersion       = "4.8.1"
val htmlUnitDriverVersion = "4.7.2"

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

ThisBuild / versionScheme := Some("early-semver")
ThisBuild / version ~= addVersionPadding

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

/**
 * Add padding to change: 0.1.0+48-bfcea99ap20220317-1157-SNAPSHOT into
 * 0.1.0+0048-bfcea99ap20220317-1157-SNAPSHOT. It helps to retrieve the
 * latest snapshots from
 * https://oss.sonatype.org/#nexus-search;gav~dev.doamaral~zio-selenium_2.13~~~~kw,versionexpand.
 */
def addVersionPadding(baseVersion: String): String = {
  import scala.util.matching.Regex

  val paddingSize    = 5
  val counter: Regex = "\\+([0-9]+)-".r

  counter.findFirstMatchIn(baseVersion) match {
    case Some(regex) =>
      val count          = regex.group(1)
      val snapshotNumber = "0" * (paddingSize - count.length) + count
      counter.replaceFirstIn(baseVersion, s"+$snapshotNumber-")
    case None => baseVersion
  }
}
