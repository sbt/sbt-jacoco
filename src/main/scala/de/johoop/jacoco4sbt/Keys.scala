package de.johoop.jacoco4sbt

import sbt._
import Keys._

trait Keys {
  lazy val Config = config("jacoco") hide

  lazy val targetDirectory = SettingKey[File]("jacoco-target-directory", "Where JaCoCo should store its execution data and reports.")
  lazy val sourceTabWidth = SettingKey[Int]("jacoco-source-tab-width", "Tab width of the sources to display in the JaCoCo report.")
  lazy val sourceEncoding = SettingKey[String]("jacoco-source-encoding", "Encoding of the source files (for JaCoCo reporting).")
  lazy val reportEncoding = SettingKey[String]("jacoco-report-encoding", "Encoding of the JaCoCo report.")
  lazy val reportTitle = SettingKey[String]("jacoco-report-title", "Title of the JaCoCo report.")
  
  lazy val jacocoClasspath = TaskKey[Classpath]("jacoco-classpath", "Internal JaCoCo classpath.")
  lazy val jacocoReport = TaskKey[Unit]("jacoco-report", "Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")

  lazy val unpackJacocoAgent = TaskKey[File]("unpack-jacoco-agent", "Unpacks the Jacoco Agent JAR")
}
