name := "play-scalacache-module"

version := "0.1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

val scVersion = "0.7.4"

libraryDependencies ++= Seq(
  cache,
  "com.github.cb372" %% "scalacache-core" % scVersion,
  specs2 % Test,
  "com.github.cb372" %% "scalacache-caffeine" % scVersion % Test
)
