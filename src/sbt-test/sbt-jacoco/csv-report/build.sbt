name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.14"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

jacocoReportSettings in Test := JacocoReportSettings()
  .withFormats(
    JacocoReportFormats.CSV
  )
