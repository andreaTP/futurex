
ThisBuild / scalaVersion := "2.13.5"

lazy val before = project.in(file("before"))
  .settings(
    scalafmtOnCompile := true,
    run / fork := false
  )

lazy val futurex = project.in(file("futurex"))
  .settings(
    libraryDependencies += "com.lihaoyi" %% "sourcecode" % "0.2.3",
    libraryDependencies += "org.scala-lang" % "scala-reflect" % (ThisBuild / scalaVersion).value % Provided,
    scalafmtOnCompile := true,
    run / fork := false
  )

lazy val after = project.in(file("after"))
  .settings(
    scalafmtOnCompile := true,
    run / fork := false
  ).dependsOn(futurex)
