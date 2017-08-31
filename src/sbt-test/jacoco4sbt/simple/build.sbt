import de.johoop.jacoco4sbt.Thresholds

name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.10.4"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

jacoco.settings
fork in jacoco.Config := false
jacoco.thresholds in jacoco.Config := Thresholds(
  instruction = 100,
  method = 100,
  branch = 100,
  complexity = 100,
  line = 100,
  clazz = 100)
