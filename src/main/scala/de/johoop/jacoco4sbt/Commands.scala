/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011 Joachim Hofer
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
import CommandSupport.logger

trait Commands extends Keys with CommandGrammar with Instrumentation with Utils {
  private[jacoco4sbt] lazy val jacocoCommand = Command("jacoco")(_ => Grammar) { (buildState, arguments) =>

    implicit val implicitState = buildState

    arguments match {
      case "instrument" => instrument
      case "persist" => persistCoverageData
      case "uninstrument" => uninstrument
      case "clean" => cleanUp
      
      case formats : ReportFormatResult => {
        val reportFormatsToGenerate = for {
          formatTuple <- formats
          (format, maybeEncoding) = formatTuple
          encoding = maybeEncoding map (_.mkString) getOrElse "utf-8"
          reportFormat = FormattedReport(format, encoding) 
        } yield reportFormat
        
        if (reportFormatsToGenerate.isEmpty) report
        else {
          val temporaryBuildStateForReports = addSetting(reportFormats in Config := reportFormatsToGenerate)
          report(temporaryBuildStateForReports)
        }
        
        buildState
      }
    }
  }

  def persistCoverageData(implicit buildState: State) = {
    import java.io.FileOutputStream
    import org.jacoco.core.data.ExecutionDataWriter

    val jacocoRuntime = getSetting(runtime).get
    doInJacocoDirectory { jacocoDirectory =>
      IO createDirectory jacocoDirectory
      val executionDataStream = new FileOutputStream(jacocoDirectory / "jacoco.exec")
      try {
        logger(buildState) info "writing execution data to " + jacocoDirectory / "jacoco.exec"
        val executionDataWriter = new ExecutionDataWriter(executionDataStream)
        jacocoRuntime.collect(executionDataWriter, null, true)
        executionDataStream.flush()
      } finally {
        executionDataStream.close()
      }
      jacocoRuntime.shutdown()

      buildState
    }
  }
  
  def uninstrument(implicit buildState: State) = {
    logger(buildState) info "Uninstrumenting the run tasks."

    // TODO delete instrumented class files, remove settings
    
    buildState
  }

  def cleanUp(implicit buildState: State) = {
    logger(buildState) info "Cleaning JaCoCo directory."
    
    doInJacocoDirectory { jacocoDirectory => 
      IO delete jacocoDirectory
      buildState
    }
  }
  
  def report(implicit buildState: State) = {
    logger(buildState) info "Generating JaCoCo coverage report(s)."
    logger(buildState) debug ("jacoco report formats: " + getSetting(reportFormats))

    Project.evaluateTask(jacocoReport in Config, buildState)
  }
}