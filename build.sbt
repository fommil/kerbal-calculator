//import scalariform.formatter.preferences._

organization := "com.github.fommil"

name := "kerbal"

scalaVersion := "2.11.5"

version := "1.0-SNAPSHOT"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.3" % "test"
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

fork := true

maxErrors := 1

// waiting on
// https://github.com/mdr/scalariform/issues/98
// https://github.com/mdr/scalariform/issues/75
// scalariformSettings

// ScalariformKeys.preferences := ScalariformKeys.preferences.value
//   .setPreference(DoubleIndentClassDeclaration, false)
//   .setPreference(PreserveDanglingCloseParenthesis, true)

// BROKEN https://github.com/scala-js/scala-js/issues/1446
//scalaJSSettings
