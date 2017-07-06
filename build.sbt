organization in ThisBuild := "com.github.fommil"
version in ThisBuild := "1.0-SNAPSHOT"
scalaVersion in ThisBuild := "2.12.2"

val scalatestVersion = "3.0.3"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",
  "-feature",
  "-deprecation",
  "-Xfatal-warnings",
  "-language:postfixOps",
  "-language:implicitConversions"
)

lazy val sharedSettings = scalariformSettings ++ Seq(
  unmanagedSourceDirectories in Compile += baseDirectory.value / "../shared/src/main/scala",
  unmanagedSourceDirectories in Test += baseDirectory.value / "../shared/src/test/scala"
)

lazy val js = project.in(file("js"))
  .enablePlugins(ScalaJSPlugin)
  .settings(sharedSettings: _*)
  .settings(
    //skip in packageJSDependencies := false,
    //scalaJSStage := FastOptStage,
    //jsDependencies += RuntimeDOM,
    resolvers += Resolver.url(
      "scala-js-releases",
      url("http://dl.bintray.com/scala-js/scala-js-releases/")
    )(
      Resolver.ivyStylePatterns
    ),
    libraryDependencies ++= Seq(
      "be.doeraene" %%% "scalajs-jquery" % "0.9.2",
      "com.lihaoyi" %%% "scalatags" % "0.6.5",
      "org.scalatest" %%% "scalatest" % scalatestVersion % "test"
    )
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
