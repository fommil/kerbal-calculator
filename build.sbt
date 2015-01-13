// note to self: build with 'sbt fastOptJS'

lazy val uTestFramework = new TestFramework("utest.runner.JvmFramework")

lazy val sharedSettings = Seq(
  organization := "com.github.fommil",
  name := "kerbal",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.5",
  scalacOptions in Compile ++= Seq(
    "-encoding", "UTF-8",
    "-target:jvm-1.6",
    "-feature",
    "-deprecation",
    "-Xfatal-warnings",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  unmanagedSourceDirectories in Compile += baseDirectory.value / "../shared/src/main/scala",
  unmanagedSourceDirectories in Test += baseDirectory.value / "../shared/src/test/scala",
  libraryDependencies += "com.lihaoyi" %% "utest" % "0.2.4" % "test",
  testFrameworks += uTestFramework,
  // WORKAROUND https://github.com/lihaoyi/utest/issues/50
  testOptions in Test += Tests.Argument(uTestFramework, "--color=false")
)

lazy val js = project.in(file("js"))
  .settings(scalaJSSettings: _*)
  .settings(utest.jsrunner.Plugin.utestJsSettings: _*)
  .settings(sharedSettings: _*)
  .settings(
    //skip in ScalaJSKeys.packageJSDependencies := false,
    ScalaJSKeys.jsDependencies += scala.scalajs.sbtplugin.RuntimeDOM,
    libraryDependencies ++= Seq(
      "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",
      "com.scalatags" %%% "scalatags" % "0.4.2"
    )
  )

lazy val jvm = project.in(file("jvm"))
  .settings(sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.3" % "test"
    )
  )
