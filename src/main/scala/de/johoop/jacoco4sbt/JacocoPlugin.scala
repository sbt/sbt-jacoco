package de.johoop.jacoco4sbt

import sbt._
import Keys._

object JacocoPlugin extends Plugin {
  object jacoco extends Commands {

    val dependencies = Seq(
      "org.jacoco" % "org.jacoco.agent" % "0.5.3.201107060350" % "jacoco" artifacts(Artifact("org.jacoco.agent", "jar", "jar")),
      "org.jacoco" % "org.jacoco.core" % "0.5.3.201107060350" % "jacoco" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
      "org.jacoco" % "org.jacoco.report" % "0.5.3.201107060350" % "jacoco" artifacts(Artifact("org.jacoco.report", "jar", "jar")))

    def unpackAgentAction(libManagedJacoco: File, classpath: Classpath) = {
      val outerAgentJar = classpath.files find (_.getName contains "agent")
      IO.unzip(outerAgentJar.get, libManagedJacoco, "*.jar").head
    }

    val settings : Seq[Setting[_]] = Seq(
      commands += jacocoCommand,
      ivyConfigurations += Config,
      libraryDependencies ++= dependencies,
      jacocoClasspath <<= (classpathTypes, update) map { Classpaths managedJars (Config, _, _) },
      unpackAgent <<= (managedDirectory in Config, jacocoClasspath) map unpackAgentAction)
  }
}
