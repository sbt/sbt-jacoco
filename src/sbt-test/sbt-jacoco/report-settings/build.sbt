name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.17"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % "test"

Test / jacocoReportSettings := JacocoReportSettings()
  .withFormats(
    JacocoReportFormats.ScalaHTML.withoutBranches,
    JacocoReportFormats.XML,
    JacocoReportFormats.CSV
  )
