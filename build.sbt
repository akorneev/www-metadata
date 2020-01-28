name := "www-metadata"
ThisBuild / organization := "com.github.akorneev"
ThisBuild / version := "0.1-SNAPSHOT"

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
