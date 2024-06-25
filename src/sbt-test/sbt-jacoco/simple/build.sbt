name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.17"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"

jacocoReportSettings := JacocoReportSettings()
  .withThresholds(
    JacocoThresholds(instruction = 100, method = 100, branch = 100, complexity = 100, line = 100, clazz = 100)
  )
