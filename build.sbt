//import scalariform.formatter.preferences._

organization := "com.github.fommil"

name := "kerbal"

scalaVersion := "2.11.5"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scala-lang.modules.scalajs" %%% "scalajs-jquery" % "0.6",
  // no scalatest in scalajs :-(
  "com.lihaoyi" %% "utest" % "0.2.4" % "test"
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

// waiting on
// https://github.com/mdr/scalariform/issues/98
// https://github.com/mdr/scalariform/issues/75
// scalariformSettings
//
// ScalariformKeys.preferences := ScalariformKeys.preferences.value
//   .setPreference(DoubleIndentClassDeclaration, false)
//   .setPreference(PreserveDanglingCloseParenthesis, true)

scalaJSSettings

skip in ScalaJSKeys.packageJSDependencies := false

ScalaJSKeys.jsDependencies += scala.scalajs.sbtplugin.RuntimeDOM

testFrameworks += new TestFramework("utest.runner.JvmFramework")

//utest.jsrunner.Plugin.utestJsSettings
