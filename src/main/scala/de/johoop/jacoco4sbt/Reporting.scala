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
  def saveDataAction(jacocoDirectory: File, streams: TaskStreams) = {

    import java.io.FileOutputStream
    import org.jacoco.core.data.ExecutionDataWriter

    IO createDirectory jacocoDirectory
    val jacocoFile = jacocoDirectory / "jacoco.exec"
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

trait Reporting extends JaCoCoRuntime {
  def reportAction(jacocoDirectory: File, reportFormats: Seq[FormattedReport], reportTitle: String,
      sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int,
      streams: TaskStreams) = {

    val reportDataFile = jacocoDirectory / "jacoco-merged.exec" orElse "jacoco.exec"
    streams.log debug ("Using file %s to create report" format reportDataFile.getName)

    val report = new Report(
        reportDirectory = jacocoDirectory,
        executionDataFile = reportDataFile,
        reportFormats = reportFormats,
        reportTitle = reportTitle,
        classDirectories = classDirectories,
        sourceDirectories = sourceDirectories,
        tabWidth = tabWidth,
        sourceEncoding = sourceEncoding)
    
    report.generate
  }

  class FileWithOrElse(file: File) {
    def orElse(otherFileName: String): File = if (file.exists) file else new File(file.getParent, otherFileName)
  }
  implicit def fileToFileWithOrElse(f: File): FileWithOrElse = new FileWithOrElse(f)
}