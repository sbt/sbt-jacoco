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

package com.github.sbt.jacoco.report

import sbt.Keys._
import sbt._

object ReportUtils {
  def generateReport(
      destinationDirectory: File,
      executionData: File,
      reportSettings: JacocoReportSettings,
      sourceDirectories: Seq[File],
      classDirectories: Seq[File],
      sourceSettings: JacocoSourceSettings,
      streams: TaskStreams,
      checkCoverage: Boolean = true): Unit = {

    val report = new Report(
      reportDirectory = destinationDirectory,
      executionDataFiles = Seq(executionData),
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      sourceSettings = sourceSettings,
      reportSettings = reportSettings,
      streams = streams,
      checkCoverage = checkCoverage
    )

    report.generate()
  }

  def generateAggregateReport(
      destinationDirectory: File,
      executionDataFiles: Seq[File],
      reportSettings: JacocoReportSettings,
      sourceDirectories: Seq[File],
      classDirectories: Seq[File],
      sourceSettings: JacocoSourceSettings,
      streams: TaskStreams,
      checkCoverage: Boolean = true): Unit = {

    val report = new Report(
      reportDirectory = destinationDirectory,
      executionDataFiles = executionDataFiles,
      classDirectories = classDirectories,
      sourceDirectories = sourceDirectories,
      sourceSettings = sourceSettings,
      reportSettings = reportSettings,
      streams = streams,
      checkCoverage = checkCoverage
    )

    report.generate()
  }
}
