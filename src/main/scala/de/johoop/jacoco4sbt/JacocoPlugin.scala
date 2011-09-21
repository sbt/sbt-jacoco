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
import java.io.FileInputStream

object JacocoPlugin extends Plugin {
  object jacoco extends Commands with Keys {

    val dependencies = Seq(
      "org.jacoco" % "org.jacoco.agent" % "0.5.3.201107060350" % "jacoco->default" artifacts(Artifact("org.jacoco.agent", "jar", "jar")))

    def unpackAgentAction(libManagedJacoco: File, classpath: Classpath) = {
      val outerAgentJar = classpath.files find (_.getName contains "agent")
      IO.unzip(outerAgentJar.get, libManagedJacoco, "*.jar").head
    }

    def instrumentAction(jacocoDirectory: File, classDirectories: Seq[File]) = {
      import org.jacoco.core.runtime.LoggerRuntime
      import org.jacoco.core.instr.Instrumenter
      import PathFinder._
      
      val runtime = new LoggerRuntime
      val instrumenter = new Instrumenter(runtime)
      
      (PathFinder(classDirectories) ** "*.class").get foreach { file =>
        println(file)
        val classStream = new FileInputStream(file)
        try instrumenter.instrument(classStream) finally classStream.close()
      }
    }
    
    def reportAction(jacocoDirectory: File, reportFormats: Seq[FormattedReport], reportTitle: String,
        sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int) = {
      
      val report = new Report(
          reportDirectory = jacocoDirectory,
          executionDataFile = jacocoDirectory / "jacoco.exec",
          reportFormats = reportFormats,
          reportTitle = reportTitle,
          classDirectories = classDirectories,
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
        reportFormats := Seq(HTMLReport()),
        reportTitle := "Jacoco Coverage Report",
        sourceTabWidth := 2,
        sourceEncoding := "utf-8",
        
        classDirectories <<= (classDirectory in Compile, classDirectory in Test) map (Seq(_, _)),
        jacocoSources <<= (sourceDirectories in Compile, sourceDirectories in Test) map (_++_),
          
        jacocoClasspath <<= (classpathTypes, update) map { Classpaths managedJars (Config, _, _) },
        unpackJacocoAgent <<= (managedDirectory in Config, jacocoClasspath in Config) map unpackAgentAction,
        
        jacocoInstrument <<= (outputDirectory, classDirectories) map instrumentAction,
        
        jacocoReport <<= 
            (outputDirectory, reportFormats, reportTitle, 
                jacocoSources, classDirectories, sourceEncoding, sourceTabWidth) map reportAction))
  }
}
