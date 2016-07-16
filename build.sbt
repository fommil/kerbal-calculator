lazy val uTestFramework = new TestFramework("utest.runner.Framework")

lazy val sharedSettings = scalariformSettings ++ Seq(
  organization := "com.github.fommil",
  name := "kerbal",
  version := "1.0-SNAPSHOT",
  scalaVersion := "2.11.8",
  scalacOptions in Compile ++= Seq(
    "-encoding", "UTF-8",
    "-feature",
    "-deprecation",
    "-Xfatal-warnings",
    "-language:postfixOps",
    "-language:implicitConversions"
  ),
  unmanagedSourceDirectories in Compile += baseDirectory.value / "../shared/src/main/scala",
  unmanagedSourceDirectories in Test += baseDirectory.value / "../shared/src/test/scala",
  libraryDependencies += "com.lihaoyi" %%% "utest" % "0.4.3" % "test",
  testFrameworks += uTestFramework,
  // WORKAROUND https://github.com/lihaoyi/utest/issues/50
  testOptions in Test += Tests.Argument(uTestFramework, "--color=false")
)

lazy val js = project.in(file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(sharedSettings: _*)
  .settings(
    //skip in packageJSDependencies := false,
    //scalaJSStage := FastOptStage,
    jsDependencies += RuntimeDOM,
    resolvers += Resolver.url(
      "scala-js-releases",
      url("http://dl.bintray.com/scala-js/scala-js-releases/")
    )(
      Resolver.ivyStylePatterns
    ),
    libraryDependencies ++= Seq(
      "be.doeraene" %%% "scalajs-jquery" % "0.9.0",
      "com.lihaoyi" %%% "scalatags" % "0.6.0"
    )
  )

lazy val jvm = project.in(file("jvm"))
  .settings(sharedSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6" % "test"
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
