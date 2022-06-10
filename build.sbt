ThisBuild / organization := "com.github.fommil"
ThisBuild / scalaVersion := "2.13.8"

val scalatestVersion = "3.2.12"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-Xfatal-warnings",
  "-language:postfixOps",
  "-language:implicitConversions"
)

lazy val sharedSettings = Seq(
  Compile / unmanagedSourceDirectories += baseDirectory.value / "../shared/src/main/scala",
  Test / unmanagedSourceDirectories += baseDirectory.value / "../shared/src/test/scala"
)

lazy val js = project.in(file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "net.exoego" %%% "scalajs-jquery3" % "2.2.0",
      "net.exoego" %%% "scalajs-jquery3-compat" % "2.2.0",
      "com.lihaoyi" %%% "scalatags" % "0.11.1",
      "org.scalatest" %%% "scalatest" % scalatestVersion % "test"
    ),
    scalaJSUseMainModuleInitializer := true
  )

lazy val jvm = project.in(file("jvm"))
  .settings(sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % scalatestVersion % "test"
    )
  )

lazy val root = project.in(file("."))
  .aggregate(js, jvm)
  .settings(
    sharedSettings: _*
  ).settings(
    publish := {},
    publishLocal := {}
  )
