package de.johoop.jacoco4sbt

import sbt._
import Keys._

import org.jacoco.core.runtime.LoggerRuntime

trait Reporting extends JaCoCoRuntime {
  def reportAction(jacocoDirectory: File, reportFormats: Seq[FormattedReport], reportTitle: String,
      sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int,
      streams: TaskStreams) = {

    import java.io.FileOutputStream
    import org.jacoco.core.data.ExecutionDataWriter

    IO createDirectory jacocoDirectory
    val jacocoFile = jacocoDirectory / "jacoco.exec"
    val executionDataStream = new FileOutputStream(jacocoFile)
    try {
      streams.log.debug("writing execution data to " + jacocoFile)
      val executionDataWriter = new ExecutionDataWriter(executionDataStream)
      runtimeData.collect(executionDataWriter, executionDataWriter, true)
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
}