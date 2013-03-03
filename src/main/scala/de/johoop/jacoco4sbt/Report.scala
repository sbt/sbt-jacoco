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
import org.jacoco.report._
import html.HTMLFormatter

import java.io.File
import java.io.FileInputStream

class Report(executionDataFile: File, classDirectories: Seq[File], 
    sourceDirectories: Seq[File], sourceEncoding: String, tabWidth: Int, 
    reportFormats: Seq[FormattedReport], reportTitle: String, reportDirectory: File) {

  def generate : Unit = {
    val (executionDataStore, sessionInfoStore) = loadExecutionData
    val bundleCoverage = analyzeStructure(executionDataStore, sessionInfoStore)
    
    reportFormats foreach (createReport(_, bundleCoverage, executionDataStore, sessionInfoStore))
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
    val analyzer = new Analyzer(executionDataStore, coverageBuilder)

    classDirectories foreach { analyzer analyzeAll _ }

    coverageBuilder getBundle reportTitle
  }

  private def createReport(reportFormat: FormattedReport, bundleCoverage: IBundleCoverage, 
      executionDataStore: ExecutionDataStore, sessionInfoStore: SessionInfoStore) = {

    val visitor = reportFormat.visitor(reportDirectory)
    
    visitor.visitInfo(sessionInfoStore.getInfos, executionDataStore.getContents);
    visitor.visitBundle(bundleCoverage, new DirectoriesSourceFileLocator(sourceDirectories, sourceEncoding, tabWidth)) 

    visitor.visitEnd()
  }

}
