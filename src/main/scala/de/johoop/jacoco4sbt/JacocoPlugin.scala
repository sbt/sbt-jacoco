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
import org.jacoco.core.runtime.LoggerRuntime

object JacocoPlugin extends Plugin {
  object jacoco extends Commands with Keys {

    def reportAction(jacocoDirectory: File, reportFormats: Seq[FormattedReport], reportTitle: String,
        sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int,
        streams: TaskStreams) = {

      import java.io.FileOutputStream
      import org.jacoco.core.data.ExecutionDataWriter

      IO createDirectory jacocoDirectory
      val executionDataStream = new FileOutputStream(jacocoDirectory / "jacoco.exec")
      try {
        streams.log.info("writing execution data to " + jacocoDirectory / "jacoco.exec")
        val executionDataWriter = new ExecutionDataWriter(executionDataStream)
        runtime.collect(executionDataWriter, null, true)
        executionDataStream.flush()
      } finally {
        executionDataStream.close()
      }

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

    val settings = Seq(ivyConfigurations += Config) ++
      inConfig(Config)(Defaults.testTasks ++ Seq( 

      outputDirectory <<= (crossTarget) { _ / "jacoco" },
      reportFormats := Seq(HTMLReport()),
      reportTitle := "Jacoco Coverage Report",
      sourceTabWidth := 2,
      sourceEncoding := "utf-8",
      
      classesToCover <<= (classDirectory in Compile) map (Seq(_)),
      coveredSources <<= (sourceDirectories in Compile) map identity,
      instrumentedClassDirectory <<= (outputDirectory, classDirectory in Compile) (_ / _.getName),

      report <<= (outputDirectory, reportFormats, reportTitle, coveredSources, classesToCover, 
          sourceEncoding, sourceTabWidth, streams) map reportAction,

      definedTests <<= definedTests in Test,
      fullClasspath <<= (products in Compile, fullClasspath in Test, instrumentedClassDirectory, streams) map instrumentAction))
  }
}
