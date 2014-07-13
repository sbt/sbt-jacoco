sbtPlugin := true

name := "jacoco4sbt"

organization := "de.johoop"

version := "2.1.6-SNAPSHOT"

resolvers += "Sonatype Release" at "https://oss.sonatype.org/content/repositories/releases"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.jacoco" % "org.jacoco.core" % "0.7.0.201403182114" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
  "org.jacoco" % "org.jacoco.report" % "0.7.0.201403182114" artifacts(Artifact("org.jacoco.report", "jar", "jar")),
  "org.specs2" %% "specs2" % "2.3.11" % "test",
  "org.mockito" % "mockito-all" % "1.9.5" % "test",
  "org.pegdown" % "pegdown" % "1.2.1" % "test")
   
scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:_")

buildInfoSettings

sourceGenerators in Compile <+= buildInfo

buildInfoKeys := Seq[BuildInfoKey](resourceDirectory in Test)

buildInfoPackage := "de.johoop.jacoco4sbt.build"

test in Test <<= test in Test dependsOn publishLocal

parallelExecution in Test := false
