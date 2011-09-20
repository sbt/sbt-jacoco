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

    def reportAction(jacocoDirectory: File, reportFormat: FormattedReport, sourceDirectories: Seq[File], 
        classDirectory: File, sourceEncoding: String, tabWidth: Int) = {
      
      val report = new Report(
          reportDirectory = jacocoDirectory,
          executionDataFile = jacocoDirectory / "jacoco.exec",
          reportFormat = reportFormat,
          classDirectory = classDirectory,
          sourceDirectories = sourceDirectories,
          tabWidth = tabWidth,
          sourceEncoding = sourceEncoding)
      
      report.generate
    }

    val settings : Seq[Setting[_]] = Seq(
      commands += jacocoCommand,
      ivyConfigurations += Config,
      libraryDependencies ++= dependencies) ++ inConfig(Config)(Seq(
        outputDirectory <<= (crossTarget) { _ / "jacoco" },
        reportFormat := HTMLReport(),
        sourceTabWidth := 2,
        sourceEncoding := "utf-8",
        
        jacocoClasspath <<= (classpathTypes, update) map { Classpaths managedJars (Config, _, _) },
        unpackJacocoAgent <<= (managedDirectory in Config, jacocoClasspath in Config) map unpackAgentAction,
        
        jacocoReport <<= 
            (outputDirectory, reportFormat, sourceDirectories in Compile, classDirectory in Compile, sourceEncoding,
                sourceTabWidth) map reportAction))
  }
}
