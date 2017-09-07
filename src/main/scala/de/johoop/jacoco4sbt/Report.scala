/*
 * This file is part of jacoco4sbt.
 *
 * Copyright (c) Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package de.johoop.jacoco4sbt

import java.io.File
import java.text.DecimalFormat

import de.johoop.jacoco4sbt.filter.FilteringAnalyzer
import de.johoop.jacoco4sbt.report.formats.JacocoReportFormat
import de.johoop.jacoco4sbt.report.{JacocoReportSettings, JacocoSourceSettings}
import org.jacoco.core.analysis._
import org.jacoco.core.data._
import org.jacoco.core.tools.ExecFileLoader
import sbt.Keys._

class Report(
    executionDataFiles: Seq[File],
    classDirectories: Seq[File],
    sourceDirectories: Seq[File],
    sourceSettings: JacocoSourceSettings,
    reportSettings: JacocoReportSettings,
    reportDirectory: File,
    streams: TaskStreams) {

  private val percentageFormat = new DecimalFormat("#.##")

  def generate(): Unit = {
    val (executionDataStore, sessionInfoStore) = loadExecutionData
    val bundleCoverage = analyzeStructure(executionDataStore, sessionInfoStore)

    reportSettings.formats.foreach(createReport(_, bundleCoverage, executionDataStore, sessionInfoStore))

    if (!checkCoverage(bundleCoverage)) {
      streams.log error "Required coverage is not met"
      // is there a better way to fail build?
      sys.exit(1)
    }
  }

  private[jacoco4sbt] def checkCoverage(bundle: IBundleCoverage) = {
    streams.log info ""
    streams.log info s"------- ${reportSettings.title} --------"
    streams.log info ""
    val checkResult = checkCounter("Lines", bundle.getLineCounter, reportSettings.thresholds.line) ::
      checkCounter("Instructions", bundle.getInstructionCounter, reportSettings.thresholds.instruction) ::
      checkCounter("Branches", bundle.getBranchCounter, reportSettings.thresholds.branch) ::
      checkCounter("Methods", bundle.getMethodCounter, reportSettings.thresholds.method) ::
      checkCounter("Complexity", bundle.getComplexityCounter, reportSettings.thresholds.complexity) ::
      checkCounter("Class", bundle.getClassCounter, reportSettings.thresholds.clazz) ::
      Nil
    streams.log info s"Check $reportDirectory for detail report"
    streams.log info ""
    !(checkResult contains false)
  }

  private[jacoco4sbt] def checkCounter(unit: String, c: ICounter, required: Double) = {
    val missedCount = c.getMissedCount
    val totalCount = c.getTotalCount
    val coveredRatio = if (c.getCoveredRatio.isNaN) 0 else c.getCoveredRatio
    val ratioPercent = coveredRatio * 100
    val success = ratioPercent >= required
    val sign = if (success) ">=" else "<"
    val status = if (success) "OK" else "NOK"
    val formattedRatio = percentageFormat.format(ratioPercent)
    streams.log.info(
      s"$unit: $formattedRatio% ($sign required $required%) covered, $missedCount of $totalCount missed, $status")
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
