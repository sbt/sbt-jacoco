resolvers += Resolver.url("Typesafe repository - Snapshots", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-snapshots/"))(Patterns(false, "[organisation]/[module]/[revision]/jars/[artifact].[ext]"))

resolvers += Resolver.url("Typesafe repository - Releases", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Patterns(false, "[organisation]/[module]/[revision]/jars/[artifact].[ext]"))

sbtPlugin := true

name := "jacoco4sbt"

organization := "de.johoop"

version := "1.2.2"

libraryDependencies ++= Seq(
  "org.jacoco" % "org.jacoco.core" % "0.5.6.201201232323" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
  "org.jacoco" % "org.jacoco.report" % "0.5.6.201201232323" artifacts(Artifact("org.jacoco.report", "jar", "jar")))
   
scalacOptions ++= Seq("-unchecked", "-deprecation")

seq(ScriptedPlugin.scriptedSettings: _*)

publishTo <<= version { v: String =>
  val nexus = "https://oss.sonatype.org/"
  if (v.trim.endsWith("SNAPSHOT"))
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishMavenStyle := true

publishArtifact in Test := false

pomIncludeRepository := { x => false }

pomExtra := (
  <url>http://www.bitbucket.org/jmhofer/findbugs4sbt</url>
  <licenses>
    <license>
      <name>Eclipse Public License v1.0</name>
      <url>http://www.eclipse.org/legal/epl-v10.html</url>
      <distribution>repo</distribution>
    </license>
  </licenses>
  <scm>
    <url>https://bitbucket.org/jmhofer/findbugs4sbt</url>
    <connection>scm:hg:https://bitbucket.org/jmhofer/findbugs4sbt</connection>
  </scm>
  <developers>
    <developer>
      <id>johofer</id>
      <name>Joachim Hofer</name>
      <url>http://jmhofer.johoop.de</url>
    </developer>
  </developers>
)

