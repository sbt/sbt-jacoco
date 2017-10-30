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

import java.io.File
import java.text.DecimalFormat

import org.jacoco.core.analysis._
import org.jacoco.core.data._
import org.jacoco.core.tools.ExecFileLoader
import com.github.sbt.jacoco.filter.FilteringAnalyzer
import com.github.sbt.jacoco.report.formats.JacocoReportFormat
import sbt.Keys._

class Report(
    executionDataFiles: Seq[File],
    classDirectories: Seq[File],
    sourceDirectories: Seq[File],
    sourceSettings: JacocoSourceSettings,
    reportSettings: JacocoReportSettings,
    reportDirectory: File,
    streams: TaskStreams,
    checkCoverage: Boolean) {

  private val percentageFormat = new DecimalFormat("#.##")

  def generate(): Unit = {
    val (executionDataStore, sessionInfoStore) = loadExecutionData
    val bundleCoverage = analyzeStructure(executionDataStore, sessionInfoStore)

    reportSettings.formats.foreach(createReport(_, bundleCoverage, executionDataStore, sessionInfoStore))

    if (checkCoverage && !checkCoverage(bundleCoverage)) {
      sys.error("Required coverage is not met")
    }
  }

  def checkCoverage(bundle: IBundleCoverage): Boolean = {
    val sb = StringBuilder.newBuilder

    sb ++= "\n------- "
    sb ++= reportSettings.title
    sb ++= " -------\n\n"

    val checkResult = checkCounter("Lines", bundle.getLineCounter, reportSettings.thresholds.line, sb) ::
      checkCounter("Instructions", bundle.getInstructionCounter, reportSettings.thresholds.instruction, sb) ::
      checkCounter("Branches", bundle.getBranchCounter, reportSettings.thresholds.branch, sb) ::
      checkCounter("Methods", bundle.getMethodCounter, reportSettings.thresholds.method, sb) ::
      checkCounter("Complexity", bundle.getComplexityCounter, reportSettings.thresholds.complexity, sb) ::
      checkCounter("Class", bundle.getClassCounter, reportSettings.thresholds.clazz, sb) ::
      Nil

    sb ++= "\nCheck "
    sb ++= reportDirectory.getAbsolutePath
    sb ++= " for detailed report\n "
    streams.log.info(sb.toString())

    !(checkResult contains false)
  }

  private[jacoco] def checkCounter(
      unit: String,
      c: ICounter,
      required: Double,
      summaryBuilder: StringBuilder): Boolean = {

    val missedCount = c.getMissedCount
    val totalCount = c.getTotalCount
    val coveredRatio = if (c.getCoveredRatio.isNaN) 0 else c.getCoveredRatio
    val ratioPercent = coveredRatio * 100
    val success = ratioPercent >= required
    val sign = if (success) ">=" else "<"
    val status = if (success) "OK" else "NOK"
    val formattedRatio = percentageFormat.format(ratioPercent)

    summaryBuilder ++= unit
    summaryBuilder ++= ": "
    summaryBuilder ++= formattedRatio
    summaryBuilder ++= "% ("
    summaryBuilder ++= sign
    summaryBuilder ++= " required "
    summaryBuilder ++= required.toString
    summaryBuilder ++= "%) covered, "
    summaryBuilder ++= missedCount.toString
    summaryBuilder ++= " of "
    summaryBuilder ++= totalCount.toString
    summaryBuilder ++= " missed, "
    summaryBuilder ++= status
    summaryBuilder ++= "\n"

    success
  }

  private def loadExecutionData = {
    val loader = new ExecFileLoader
    executionDataFiles foreach { f =>
      // it is possible that there's no test at all, thus no executionDataFile
      if (f.exists) loader.load(f)
    }

    (loader.getExecutionDataStore, loader.getSessionInfoStore)
  }

  private def analyzeStructure(executionDataStore: ExecutionDataStore, sessionInfoStore: SessionInfoStore) = {
    val coverageBuilder = new CoverageBuilder
    val analyzer = new FilteringAnalyzer(executionDataStore, coverageBuilder)

    classDirectories.foreach(analyzer.analyzeAll)

    coverageBuilder.getBundle(reportSettings.title)
  }

  private def createReport(
      reportFormat: JacocoReportFormat,
      bundleCoverage: IBundleCoverage,
      executionDataStore: ExecutionDataStore,
      sessionInfoStore: SessionInfoStore): Unit = {

    val visitor = reportFormat.createVisitor(reportDirectory, reportSettings.fileEncoding)

    visitor.visitInfo(sessionInfoStore.getInfos, executionDataStore.getContents)
    visitor.visitBundle(bundleCoverage, new DirectoriesSourceFileLocator(sourceDirectories, sourceSettings))

    visitor.visitEnd()
  }
}
