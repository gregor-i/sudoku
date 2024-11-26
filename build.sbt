name := "sudoku"

ThisBuild / scalaVersion := "3.3.0"
ThisBuild / scalacOptions ++= Seq("-feature", "-deprecation")
ThisBuild / fork := true

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
  .settings(monocle, scalatest, laminar, circe)
  .settings(
    //Compile / fastOptJS / artifactPath  := (ThisBuild / baseDirectory).value / "build" / "app.js",
    (Compile / fastOptJS) := {
      val result = (Compile / fastOptJS).value

      val file   = result.data
      val target = baseDirectory.value / ".." / "build" / "app.js"
      IO.copyFile(result.data, target)
      println(target)
      result
    }
  )

val `service-worker` = project
  .in(file("service-worker"))
  .enablePlugins(ScalaJSPlugin)
  .settings(scalaJSUseMainModuleInitializer := true)
  .enablePlugins(BuildInfoPlugin)
  .settings(
    buildInfoKeys := Seq(
      BuildInfoKey.action("buildTime") { System.currentTimeMillis },
      BuildInfoKey.action("assetFiles") { sys.env.getOrElse("ASSET_FILES", "") }
    )
  )
  .settings(scalaJsDom)

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

def laminar =
  libraryDependencies += "com.raquo" %%% "laminar" % "16.0.0"
