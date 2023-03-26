ThisBuild / version := "0.2.0-SNAPSHOT"
ThisBuild / organization := "com.codingcrafters"
ThisBuild / scalaVersion := "3.2.0"

import java.io.File

import java.nio.charset.StandardCharsets

import sbt.IO._

import scala.collection.convert.wrapAll._

ThisBuild / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case PathList("javax", "servlet", xs @ _*)         => MergeStrategy.first
  case PathList(ps @ _*) if ps.last endsWith ".class" => MergeStrategy.first
  case "module-info.class"                            => MergeStrategy.first
  case x =>
    val baseStrategy = (assemblyMergeStrategy in assembly).value
    baseStrategy(x)
}

lazy val app = (project in file("app"))
  .settings(
    assembly / mainClass := Some("com.codingcrafters.desktop.DesktopLauncher"),
  )

val libgdxVersion = "1.11.0"
logLevel := Level.Debug

name := "paint-fluidity"
version := "0.2"
scalaVersion := "3.2.0"
assetsDirectory := {
  val r = file("core/assets")
  IO.createDirectory(r)
  r
}
libraryDependencies ++= Seq(
  "com.badlogicgames.gdx" % "gdx" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-backend-lwjgl3" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-box2d-platform" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-freetype-platform" % libgdxVersion,
  "com.badlogicgames.gdx" % "gdx-tools" % libgdxVersion,

  "com.badlogicgames.gdx" % "gdx-platform" % libgdxVersion classifier "natives-desktop",
  "com.badlogicgames.gdx" % "gdx-box2d-platform" % libgdxVersion classifier "natives-desktop",
  "com.badlogicgames.gdx" % "gdx-freetype-platform" % libgdxVersion classifier "natives-desktop"
)
//javaOptions ++= Seq(
//  "-XstartOnFirstThread"
//)
javacOptions ++= Seq(
  "-Xlint",
  "-encoding", "UTF-8",
  "-source", "17",
  "-target", "17"
)
scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-rewrite", "-indent",
  "-encoding", "UTF-8",
)
exportJars := true

Compile / fork := true
run / baseDirectory := assetsDirectory.value
Compile / unmanagedResourceDirectories += assetsDirectory.value

lazy val assetsDirectory = settingKey[File]("Directory with game's assets")
