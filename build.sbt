sbtPlugin := true

publishMavenStyle := false

name := "jacoco4sbt"

organization := "de.johoop"

version := "1.0.0-SNAPSHOT"

publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

libraryDependencies ++= Seq(
    "org.scalaz" %% "scalaz-core" % "6.0.3")

scalacOptions ++= Seq("-unchecked", "-deprecation")

