name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.17"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.18" % "test"

Test / jacocoReportSettings := JacocoReportSettings()
  .withFormats(
    JacocoReportFormats.XML
  )
