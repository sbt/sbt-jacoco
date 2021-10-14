organization := "com.example"

scalaVersion := "2.12.15"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.10" % "test"
)

lazy val common = project
  .in(file("common"))

lazy val extras = project
  .in(file("extras"))

lazy val root = project
  .in(file("."))
  .aggregate(
    common,
    extras
  )
  .settings(
    publish := {},
    publishLocal := {},
    test := {},
    testOnly := {}
  )