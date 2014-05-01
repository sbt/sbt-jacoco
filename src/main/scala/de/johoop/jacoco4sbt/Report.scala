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

import org.jacoco.core.data._
import org.jacoco.core.analysis._

import java.io.File
import java.io.FileInputStream
import de.johoop.jacoco4sbt.filter.FilteringAnalyzer
import sbt.Keys._
import java.text.DecimalFormat

class Report(executionDataFile: File,
             classDirectories: Seq[File],
             sourceDirectories: Seq[File],
             sourceEncoding: String,
             tabWidth: Int,
             reportFormats: Seq[FormattedReport],
             reportTitle: String,
             reportDirectory: File,
             ratios: Map[String, Double],
             streams: TaskStreams) {

  private val format = new DecimalFormat("#.##")

  def generate : Unit = {
    val (executionDataStore, sessionInfoStore) = loadExecutionData
    val bundleCoverage = analyzeStructure(executionDataStore, sessionInfoStore)
    
    reportFormats foreach (createReport(_, bundleCoverage, executionDataStore, sessionInfoStore))

    checkCoverage(bundleCoverage)
  }

  private def checkCoverage(bundle: IBundleCoverage) =
  {
    streams.log info ""
    streams.log info s"------- $reportTitle --------"
    streams.log info ""
    val checkResult = checkCounter("Lines", bundle.getLineCounter, ratios.getOrElse("line", 0d)) ::
      checkCounter("Instructions", bundle.getInstructionCounter, ratios.getOrElse("instruction", 0d)) ::
      checkCounter("Branches", bundle.getBranchCounter, ratios.getOrElse("branch", 0d)) ::
      checkCounter("Methods", bundle.getMethodCounter, ratios.getOrElse("method", 0d)) ::
      checkCounter("Complexity", bundle.getComplexityCounter, ratios.getOrElse("complexity", 0d)) ::
      checkCounter("Class", bundle.getClassCounter, ratios.getOrElse("class", 0d)) ::
      Nil
    streams.log info s"Check $reportDirectory for detail report"
    streams.log info ""
    if (checkResult contains false)
    {
      streams.log error "Required coverage is not met"
      sys.exit(0)
    }
  }

  private def checkCounter(unit: String, c: ICounter, required: Double) =
  {
    val missedCount = c.getMissedCount
    val totalCount = c.getTotalCount
    val coveredRatio = if (c.getCoveredRatio.isNaN) 0 else c.getCoveredRatio
    val ratioPercent = coveredRatio * 100
    val success = ratioPercent >= required
    val sign = if (success) ">=" else "<"
    val status = if (success) "OK" else "NOK"
    val formattedRatio = format.format(ratioPercent)
    streams.log info s"$unit: $formattedRatio% ($sign required $required%) covered, $missedCount of $totalCount missed, $status"
    success
  }

  private def loadExecutionData = {
    val executionDataStore = new ExecutionDataStore
    val sessionInfoStore = new SessionInfoStore
    val fis = new FileInputStream(executionDataFile)
    try {
      val executionDataReader = new ExecutionDataReader(fis)

      executionDataReader setExecutionDataVisitor executionDataStore
      executionDataReader setSessionInfoVisitor sessionInfoStore

      while (executionDataReader.read()) { /* side effects galore :( */ }

    } finally {
      fis.close()
    }

    (executionDataStore, sessionInfoStore)
  }

  private def analyzeStructure(executionDataStore: ExecutionDataStore, sessionInfoStore: SessionInfoStore) = {
    val coverageBuilder = new CoverageBuilder
    val analyzer = new FilteringAnalyzer(executionDataStore, coverageBuilder)

    classDirectories foreach (analyzer analyzeAll)

    coverageBuilder getBundle reportTitle
  }

  private def createReport(reportFormat: FormattedReport, bundleCoverage: IBundleCoverage, 
      executionDataStore: ExecutionDataStore, sessionInfoStore: SessionInfoStore) = {

    val visitor = reportFormat.visitor(reportDirectory)
    
    visitor.visitInfo(sessionInfoStore.getInfos, executionDataStore.getContents)
    visitor.visitBundle(bundleCoverage, new DirectoriesSourceFileLocator(sourceDirectories, sourceEncoding, tabWidth)) 

    visitor.visitEnd()
  }

}
