/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011 Joachim Hofer
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.johoop.jacoco4sbt

import ReportFormat._

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

    def reportAction(reportFormat: ReportFormat, sourceDirectories: Seq[File], classDirectory: File, 
        sourceEncoding: String, reportEncoding: String, tabWidth: Int, reportTitle: String, jacocoDirectory: File) = {
      
      val report = new Report(
          executionDataFile = jacocoDirectory / "jacoco.exec",
          reportFormat = reportFormat,
          classDirectory = classDirectory,
          sourceDirectories = sourceDirectories,
          reportDirectory = jacocoDirectory,
          tabWidth = tabWidth,
          sourceEncoding = sourceEncoding,
          outputEncoding = reportEncoding,
          title = reportTitle)
      
      report.generate
    }

    val settings : Seq[Setting[_]] = Seq(
      commands += jacocoCommand,
      ivyConfigurations += Config,
      libraryDependencies ++= dependencies,
      
      targetDirectory <<= (crossTarget) { _ / "jacoco" },
      reportFormat := ReportFormat.HTML,
      sourceTabWidth := 2,
      sourceEncoding := "utf-8",
      reportEncoding := "utf-8",
      reportTitle := "JaCoCo Coverage Report",
      
      jacocoClasspath in Config <<= (classpathTypes, update) map { Classpaths managedJars (Config, _, _) },
      unpackJacocoAgent <<= (managedDirectory in Config, jacocoClasspath in Config) map unpackAgentAction,
      
      jacocoReport in Config <<= 
          (reportFormat, sourceDirectories in Compile, classDirectory in Compile, sourceEncoding, reportEncoding,
              sourceTabWidth, reportTitle, targetDirectory) map reportAction)
  }
}
