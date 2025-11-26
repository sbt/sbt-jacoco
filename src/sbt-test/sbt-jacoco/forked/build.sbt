name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.13.18"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"

Test / fork := true
