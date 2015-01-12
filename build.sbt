import ScalaJSKeys._

organization := "com.github.fommil"
name := "kerbal"
version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5"

libraryDependencies ++= Seq(
  "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",
  // no scalatest in scalajs :-(
  "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test"
)

scalacOptions in Compile ++= Seq(
  "-encoding", "UTF-8"
  ,"-target:jvm-1.6"
  ,"-feature"
  ,"-deprecation"
  ,"-Xfatal-warnings"
  ,"-language:postfixOps"
  ,"-language:implicitConversions"
)

maxErrors := 1

scalaJSSettings

skip in packageJSDependencies := false
jsDependencies += scala.scalajs.sbtplugin.RuntimeDOM
