name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.14"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

jacocoSourceSettings := JacocoSourceSettings(tabWidth = 4, fileEncoding = "ISO-8859-1")
