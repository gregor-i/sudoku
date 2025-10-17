name := "sudoku"

ThisBuild / scalaVersion := "3.7.3"
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation")
ThisBuild / fork := true

// projects
lazy val root = project
  .in(file("."))
  .aggregate(model.js, frontend)

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

val analytics = project
  .in(file("analytics"))
  .dependsOn(model.jvm)

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

def snabbdom = Seq(
  resolvers += "jitpack" at "https://jitpack.io",
  libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "scalajs-snabbdom" % "1.3.0" cross CrossVersion.for3Use2_13,
  libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "snabbdom-toasts"  % "1.3.0" cross CrossVersion.for3Use2_13,
  libraryDependencies += "com.github.gregor-i.scalajs-snabbdom" %%% "snabbdom-components" % "1.3.0" cross CrossVersion.for3Use2_13
)
