name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.13.16"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test"

jacocoReportSettings := JacocoReportSettings()
  .withFormats(
    JacocoReportFormats.ScalaHTML,
    JacocoReportFormats.XML
  )
  .withThresholds(
    JacocoThresholds(instruction = 100, method = 100, branch = 0, complexity = 100, line = 100, clazz = 100)
  )
