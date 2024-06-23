import scala.sys.process._
import org.scalajs.sbtplugin.Stage

name := "sudoku"

ThisBuild / scalaVersion := "3.2.1"
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
    }
  )
  .settings(monocle, scalatest, snabbdom, circe)

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

frontend / compile := Def.taskDyn {
  val stage = (frontend / Compile / scalaJSStage).value
  val ret   = (frontend / Compile / compile).value
  stage match {
    case Stage.FullOpt =>
      (frontend / Compile / fullOptJS).map {
        file =>
          Seq("./node_modules/.bin/esbuild", file.data.getAbsolutePath, "--outfile=build/app.js", "--bundle", "--minify").!
          ret
      }

    case Stage.FastOpt =>
      (frontend / Compile / fastOptJS).map {
        file =>
          Seq("./node_modules/.bin/esbuild", file.data.getAbsolutePath, "--outfile=build/app.js", "--bundle").!
          ret
      }
  }
}.value

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
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.19" % Test,
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )

def scalaJsDom =
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "2.4.0"

def snabbdom = Seq(
  resolvers += "jitpack" at "https://jitpack.io",
  libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "scalajs-snabbdom" % "1.3.0" cross CrossVersion.for3Use2_13,
  libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "snabbdom-toasts"  % "1.3.0" cross CrossVersion.for3Use2_13,
  libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "snabbdom-components" % "1.3.0" cross CrossVersion.for3Use2_13
)
