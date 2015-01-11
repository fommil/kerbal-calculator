organization := "com.github.fommil"

name := "kerbal"

scalaVersion := "2.11.5"

version := "1.0-SNAPSHOT"

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

scalariformSettings

scalaJSSettings
