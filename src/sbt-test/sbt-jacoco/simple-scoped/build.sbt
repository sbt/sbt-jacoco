name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.15"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test"

Test / jacocoReportSettings := JacocoReportSettings()
  .withThresholds(
    JacocoThresholds(instruction = 100, method = 100, branch = 100, complexity = 100, line = 100, clazz = 100)
  )
