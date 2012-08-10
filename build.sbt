sbtPlugin := true

name := "jacoco4sbt"

organization := "de.johoop"

version := "1.2.4"

libraryDependencies ++= Seq(
  "org.jacoco" % "org.jacoco.core" % "0.5.9.201207300726" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
  "org.jacoco" % "org.jacoco.report" % "0.5.9.201207300726" artifacts(Artifact("org.jacoco.report", "jar", "jar")))
   
scalacOptions ++= Seq("-unchecked", "-deprecation")

