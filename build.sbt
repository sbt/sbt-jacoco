resolvers += Resolver.url("Typesafe repository - Snapshots", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-snapshots/"))(Patterns(false, "[organisation]/[module]/[revision]/jars/[artifact].[ext]"))

resolvers += Resolver.url("Typesafe repository - Releases", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Patterns(false, "[organisation]/[module]/[revision]/jars/[artifact].[ext]"))

sbtPlugin := true

publishMavenStyle := false

name := "jacoco4sbt"

organization := "de.johoop"

version := "1.2.0-SNAPSHOT"

publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

libraryDependencies ++= Seq(
  "org.jacoco" % "org.jacoco.core" % "0.5.3.201107060350" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
  "org.jacoco" % "org.jacoco.report" % "0.5.3.201107060350" artifacts(Artifact("org.jacoco.report", "jar", "jar")))
   
scalacOptions ++= Seq("-unchecked", "-deprecation")

seq(ScriptedPlugin.scriptedSettings: _*)
