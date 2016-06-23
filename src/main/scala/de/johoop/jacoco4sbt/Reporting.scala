/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011-2013 Joachim Hofer & contributors
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

trait SavingData extends JaCoCoRuntime {
  def saveDataAction(jacocoFile: File, forked: Boolean, streams: TaskStreams) = {

    import java.io.FileOutputStream
    import org.jacoco.core.data.ExecutionDataWriter

    if (! forked) {
      IO createDirectory jacocoFile.getParentFile
      val executionDataStream = new FileOutputStream(jacocoFile)
      try {
        streams.log debug ("writing execution data to " + jacocoFile)
        val executionDataWriter = new ExecutionDataWriter(executionDataStream)
        runtimeData collect (executionDataWriter, executionDataWriter, true)
        executionDataStream.flush
      } finally {
        executionDataStream.close
      }
    }
  }
}

trait Reporting extends JaCoCoRuntime {
  def reportAction(reportDirectory: File, executionDataFile: File, reportFormats: Seq[FormattedReport], reportTitle: String,
                   sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int,
                   thresholds: Thresholds, streams: TaskStreams): Unit = {

    val report = new Report(
      reportDirectory = reportDirectory,
      executionDataFiles = Seq(executionDataFile),
      reportFormats = reportFormats,
      reportTitle = reportTitle,
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      tabWidth = tabWidth,
      sourceEncoding = sourceEncoding,
      thresholds = thresholds,
      streams = streams)

    report.generate
  }

  def aggregateReportAction(reportDirectory: File, executionDataFiles: Seq[File], reportFormats: Seq[FormattedReport], reportTitle: String,
      sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int,
      thresholds: Thresholds, streams: TaskStreams): Unit = {

    val report = new Report(
      reportDirectory = reportDirectory,
      executionDataFiles = executionDataFiles,
      reportFormats = reportFormats,
      reportTitle = reportTitle,
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      tabWidth = tabWidth,
      sourceEncoding = sourceEncoding,
      thresholds = thresholds,
      streams = streams)

    report.generate
  }

  class FileWithOrElse(file: File) {
    def orElse(otherFileName: String): File = if (file.exists) file else new File(file.getParent, otherFileName)
  }
  implicit def fileToFileWithOrElse(f: File): FileWithOrElse = new FileWithOrElse(f)
}