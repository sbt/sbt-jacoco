/*
 * This file is part of sbt-jacoco.
 *
 * Copyright (c) Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.scalasbt.jacoco.report

import sbt.Keys._
import sbt._

import scala.language.implicitConversions

private[jacoco] object Reporting {
  def reportAction(
      reportDirectory: File,
      executionDataFile: File,
      reportSettings: JacocoReportSettings,
      sourceDirectories: Seq[File],
      classDirectories: Seq[File],
      sourceSettings: JacocoSourceSettings,
      streams: TaskStreams): Unit = {

    val report = new Report(
      reportDirectory = reportDirectory,
      executionDataFiles = Seq(executionDataFile),
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      sourceSettings = sourceSettings,
      reportSettings = reportSettings,
      streams = streams
    )

    report.generate()
  }

  def aggregateReportAction(
      reportDirectory: File,
      executionDataFiles: Seq[File],
      reportSettings: JacocoReportSettings,
      sourceDirectories: Seq[File],
      classDirectories: Seq[File],
      sourceSettings: JacocoSourceSettings,
      streams: TaskStreams): Unit = {

    val report = new Report(
      reportDirectory = reportDirectory,
      executionDataFiles = executionDataFiles,
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      sourceSettings = sourceSettings,
      reportSettings = reportSettings,
      streams = streams
    )

    report.generate()
  }

  class FileWithOrElse(file: File) {
    def orElse(otherFileName: String): File = if (file.exists) file else new File(file.getParent, otherFileName)
  }
  implicit def fileToFileWithOrElse(f: File): FileWithOrElse = new FileWithOrElse(f)
}
