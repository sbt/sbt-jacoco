name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.10.4"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

jacocoReportSettings in Test := JacocoReportSettings()
  .withFormats(
    JacocoReportFormats.ScalaHTML.withoutBranches,
    JacocoReportFormats.XML,
    JacocoReportFormats.CSV
  )
