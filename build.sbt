organization := "com.github.fommil"
name := "kerbal"
version := "1.0-SNAPSHOT"

scalaVersion := "2.11.5"

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
