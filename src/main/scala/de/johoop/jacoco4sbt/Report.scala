package de.johoop.jacoco4sbt

import org.jacoco.core.data._
import org.jacoco.core.analysis._
import org.jacoco.report._
import html.HTMLFormatter

import java.io.File
import java.io.FileInputStream

class Report(executionDataFile: File, classDirectory: File, sourceDirectories: Seq[File],
    sourceEncoding: String = "utf-8", reportDirectory: File, title: String = "JaCoCo Coverage Report", 
    tabWidth: Int) {

  def generate : Unit = {
    val (executionDataStore, sessionInfoStore) = loadExecutionData
    val bundleCoverage = analyzeStructure(executionDataStore, sessionInfoStore)

    createReport(bundleCoverage, executionDataStore, sessionInfoStore)
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

    analyzer analyzeAll classDirectory

    coverageBuilder getBundle title
  }

  private def createReport(bundleCoverage: IBundleCoverage, executionDataStore: ExecutionDataStore, sessionInfoStore: SessionInfoStore) = {
    val htmlFormatter = new HTMLFormatter
    val visitor = htmlFormatter createVisitor new FileMultiReportOutput(reportDirectory)

    visitor.visitInfo(sessionInfoStore.getInfos, executionDataStore.getContents);
    visitor.visitBundle(bundleCoverage, new DirectoriesSourceFileLocator(sourceDirectories, sourceEncoding, tabWidth)) 

    visitor.visitEnd()
  }
}
