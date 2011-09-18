package de.johoop.jacoco4sbt

import sbt._
import Keys._

object JacocoPlugin extends Plugin {
  object jacoco extends Commands with Keys {

    val dependencies = Seq(
      "org.jacoco" % "org.jacoco.agent" % "0.5.3.201107060350" % "jacoco->default" artifacts(Artifact("org.jacoco.agent", "jar", "jar")))

    def unpackAgentAction(libManagedJacoco: File, classpath: Classpath) = {
      val outerAgentJar = classpath.files find (_.getName contains "agent")
      IO.unzip(outerAgentJar.get, libManagedJacoco, "*.jar").head
    }

    def reportAction = {
      // TODO different report kinds
      // TODO retrieve and fill in all the parameters
      val report = new Report(
          executionDataFile = new File("jacoco.exec"),
          classesDirectory = new File("target/scala-2.9.1/classes"),
          sourceDirectory = new File("src/main/scala"),
          reportDirectory = new File("target/scala-2.9.1/report"),
          title = "JaCoCo Coverage Report")
      
      report.generate
    }

    val settings : Seq[Setting[_]] = Seq(
      commands += jacocoCommand,
      ivyConfigurations += Config,
      libraryDependencies ++= dependencies,
      jacocoClasspath in Config <<= (classpathTypes, update) map { Classpaths managedJars (Config, _, _) },
      unpackAgent in Config <<= (managedDirectory in Config, jacocoClasspath in Config) map unpackAgentAction,
      jacocoReport in Config := reportAction)
  }
}
