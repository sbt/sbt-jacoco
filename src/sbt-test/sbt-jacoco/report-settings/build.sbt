name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.16"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test"

Test / jacocoReportSettings := JacocoReportSettings()
  .withFormats(
    JacocoReportFormats.ScalaHTML.withoutBranches,
    JacocoReportFormats.XML,
    JacocoReportFormats.CSV
  )
