name := "www-metadata"

ThisBuild / organization := "com.github.akorneev"
ThisBuild / organizationName := "Alexander Korneev"
ThisBuild / organizationHomepage := Some(url("https://github.com/akorneev"))
ThisBuild / scmInfo := Some(ScmInfo(browseUrl = url("https://github.com/akorneev/www-metadata"), connection = "scm:git@github.com:akorneev/www-metadata.git"))
ThisBuild / developers := List(Developer(id = "akorneev", name = "Alexander Korneev", email = "akorneev@gmail.com", url = url("https://github.com/akorneev")))
ThisBuild / description := "Library for working with various metadata found on the World Wide Web"
ThisBuild / licenses := List("MIT License" -> url("https://raw.githubusercontent.com/akorneev/www-metadata/master/LICENSE"))
ThisBuild / homepage := Some(url("https://github.com/akorneev/www-metadata"))

ThisBuild / version := "0.3"

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / crossScalaVersions := Seq("2.12.10", "2.13.1")

ThisBuild / scalafmtOnCompile := true

ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-opt:_",
  "-Xlint:_",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:_",
  "-Ywarn-extra-implicit"
) ++ (CrossVersion.partialVersion(scalaVersion.value) match {
  case Some((2, 11)) | Some((2, 12)) => Seq("-Xfuture", "-Ypartial-unification")
  case _                             => Seq.empty
})

val catsV       = "2.1.0"
val jsoup       = "org.jsoup" % "jsoup" % "1.12.1"
val `cats-core` = "org.typelevel" %% "cats-core" % catsV
val scalatest   = "org.scalatest" %% "scalatest" % "3.1.0"

ThisBuild / libraryDependencies ++= Seq(
  scalatest % Test
)

lazy val `www-metadata` = (project in file("."))
  .settings(
    // crossScalaVersions must be set to Nil on the aggregating project
    crossScalaVersions := Nil,
    publish / skip := true
  )
  .aggregate(`www-metadata-core`, `www-metadata-parsers`)

lazy val `www-metadata-core` = (project in file("core"))

lazy val `www-metadata-parsers` = (project in file("parsers"))
  .settings(
    libraryDependencies := libraryDependencies.value ++ Seq(
      jsoup //,
//    `cats-core`
    )
  )
  .dependsOn(`www-metadata-core`)

ThisBuild / publishTo := Some("GitHub Package Registry" at "https://maven.pkg.github.com/akorneev/www-metadata")
ThisBuild / credentials := Seq(Credentials(Path.userHome / ".sbt" / ".github_credentials"))
