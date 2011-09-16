sbtPlugin := true

publishMavenStyle := false

name := "jacoco4sbt"

organization := "de.johoop"

version := "1.0.0-SNAPSHOT"

publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

libraryDependencies ++= Seq(
    "org.jacoco" % "org.jacoco.core" % "0.5.3.201107060350" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
    "org.jacoco" % "org.jacoco.report" % "0.5.3.201107060350" artifacts(Artifact("org.jacoco.report", "jar", "jar")),
    "org.scalaz" %% "scalaz-core" % "6.0.3")

scalacOptions ++= Seq("-unchecked", "-deprecation")

