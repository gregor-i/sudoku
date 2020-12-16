import scala.sys.process._
import org.scalajs.sbtplugin.Stage

name := "sudoku"

ThisBuild / scalaVersion := "2.13.3"
scalacOptions in ThisBuild ++= Seq("-feature", "-deprecation", "-Ymacro-annotations")
scalafmtOnCompile in ThisBuild := true

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
    scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.ESModule) }
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

// tasks

compile in frontend := Def.taskDyn {
  val stage = (frontend / Compile / scalaJSStage).value
  val ret   = (frontend / Compile / compile).value
  stage match {
    case Stage.FullOpt => (frontend / Compile / fullOptJS).map { _ =>
      Seq("./node_modules/.bin/webpack", "--mode", "production").!!
      ret
    }

    case Stage.FastOpt => (frontend / Compile / fastOptJS).map { _ =>
      Seq("./node_modules/.bin/webpack", "--mode", "development").!!
      ret
    }
  }
}.value

compile in `service-worker` := Def.taskDyn {
  val stage = (`service-worker` / Compile / scalaJSStage).value
  val ret   = (`service-worker` / Compile / compile).value
  val buildFrontendTask = stage match {
    case Stage.FullOpt => (`service-worker` / Compile / fullOptJS)
    case Stage.FastOpt => (`service-worker` / Compile / fastOptJS)
  }
  streams.value.log.info(s"integrating frontend (${stage})")
  buildFrontendTask.map { buildFrontend =>
    val outputFile = "build/sw.js"
    Seq("cp", buildFrontend.data.toString, outputFile).!!
    ret
  }
}.value

compile in Compile in root := Def
  .sequential(
    (compile in Compile in frontend),
    (compile in Compile in `service-worker`)
  )
  .value

// dependencies

def monocle = {
  val version = "2.1.0"

  libraryDependencies ++= Seq(
    "com.github.julien-truffaut" %%% "monocle-core"   % version,
    "com.github.julien-truffaut" %%% "monocle-macro"  % version,
    "com.github.julien-truffaut" %%% "monocle-unsafe" % version,
    "com.github.julien-truffaut" %%% "monocle-state"  % version
  )
}

def circe = {
  val version = "0.13.0"
  libraryDependencies ++= Seq(
    "io.circe" %%% "circe-core"           % version,
    "io.circe" %%% "circe-generic"        % version,
    "io.circe" %%% "circe-generic-extras" % version,
    "io.circe" %%% "circe-parser"         % version
  )
}

def scalatest =
  Seq(
    libraryDependencies += "org.scalatest" %%% "scalatest" % "3.2.2" % Test,
    testOptions += Tests.Argument(TestFrameworks.ScalaTest, "-oD")
  )

def scalaJsDom =
  libraryDependencies += "org.scala-js" %%% "scalajs-dom" % "1.1.0"

def snabbdom = Seq(
  resolvers += Resolver.bintrayRepo("gregor-i", "maven"),
  libraryDependencies += "com.github.gregor-i" %%% "scalajs-snabbdom" % "1.1"
)
