name := "www-metadata"
ThisBuild / organization := "com.github.akorneev"
ThisBuild / version := "0.1-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.1"
ThisBuild / crossScalaVersions := Seq("2.12.10", "2.13.1")

ThisBuild / scalafmtOnCompile := true

ThisBuild / libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.1.0" % Test
)

ThisBuild / scalacOptions ++= Seq(
  "-encoding",
  "utf8",
  "-deprecation",
  "-unchecked",
  "-feature",
  "-opt:_",
  "-Xfuture",
  "-Xlint:_",
  //  "-Ypartial-unification",
  "-Ywarn-dead-code",
  "-Ywarn-value-discard",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused:_",
  "-Ywarn-extra-implicit"
)

lazy val `www-metadata` = (project in file("."))
  .aggregate(`www-metadata-core`, `www-metadata-parsers`)

lazy val `www-metadata-core` = (project in file("core"))

lazy val `www-metadata-parsers` = (project in file("parsers"))
  .settings(
    libraryDependencies := libraryDependencies.value ++ Seq(
      "org.jsoup" % "jsoup" % "1.12.1"
    )
  )
  .dependsOn(`www-metadata-core`)
