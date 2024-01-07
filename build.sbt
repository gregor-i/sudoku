import scala.sys.process.*
import org.scalajs.sbtplugin.Stage
import org.scalajs.linker.interface.ModuleSplitStyle
import sbt.Keys.libraryDependencies

name := "sudoku"

ThisBuild / scalaVersion := "3.3.1"
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation")

// projects
lazy val root = project
  .in(file("."))
  .aggregate(model.js, frontend, `service-worker`)

lazy val model = crossProject(JSPlatform, JVMPlatform)
  .crossType(CrossType.Pure)
  .in(file("model"))
  .settings(monocle, scalatest)

lazy val frontend = project
  .in(file("frontend"))
  .dependsOn(model.js)
  .enablePlugins(ScalaJSPlugin)
  .settings(
    scalaJSUseMainModuleInitializer := true,
    scalaJSLinkerConfig ~= {
      _.withModuleKind(ModuleKind.ESModule)
//      .withModuleSplitStyle(ModuleSplitStyle.SmallestModules)
    },
    libraryDependencies += "com.raquo" %%% "laminar" % "16.0.0",
    Compile / fastOptJS / artifactPath  := (ThisBuild / baseDirectory).value / "build" / "app.js",
    Compile / fullOptJS / artifactPath  := (ThisBuild / baseDirectory).value / "build" / "app.js"
  )
  .settings(monocle, scalatest, circe)

val `service-worker` = project
  .in(file("service-worker"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaJSUseMainModuleInitializer := true)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq(
      BuildInfoKey.action("buildTime") { System.currentTimeMillis },
      BuildInfoKey.action("assetFiles") { "ls build".!! }
    )
  )
  .settings(scalaJsDom)

val analytics = project
  .in(file("analytics"))
  .dependsOn(model.jvm)

// tasks

`service-worker` / compile := Def.taskDyn {
  val stage = (`service-worker` / Compile / scalaJSStage).value
  val ret   = (`service-worker` / Compile / compile).value
  val buildFrontendTask = stage match {
    case Stage.FullOpt => (`service-worker` / Compile / fullOptJS)
    case Stage.FastOpt => (`service-worker` / Compile / fastOptJS)
  }
  streams.value.log.info(s"integrating frontend (${stage})")
  buildFrontendTask.map {
    buildFrontend =>
      val outputFile = "build/sw.js"
      Seq("cp", buildFrontend.data.toString, outputFile).!!
      ret
  }
}.value

root / Compile / compile := Def
  .sequential(
    (frontend / Compile / compile),
    (`service-worker` / Compile / compile)
  )
  .value

// dependencies

def monocle = {
  val version = "3.2.0"

  libraryDependencies ++= Seq(
    "dev.optics" %%% "monocle-core"  % version,
    "dev.optics" %%% "monocle-macro" % version
  )
}

def circe = {
  val version = "0.14.1"
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core"    % version,
    "io.circe" %%% "circe-generic" % version,
    "io.circe" %%% "circe-parser"  % version
  )
}

def scalatest =
  Seq(
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.14" % Test,
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )

def scalaJsDom =
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0"
