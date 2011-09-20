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

trait Commands extends Keys with CommandGrammar {
  private[jacoco4sbt] lazy val jacocoCommand = Command("jacoco")(_ => Grammar) { (buildState, arguments) =>

    implicit val implicitState = buildState

    arguments match {
      case "instrument" => instrument 
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

  def instrument(implicit buildState: State) = {
    logger(buildState) info "Instrumenting the run tasks."

    doInJacocoDirectory { jacocoDirectory =>
      val agentFilePath = extractedState.evalTask(unpackJacocoAgent in Config, buildState).getAbsolutePath
      val executionDataPath = (jacocoDirectory / "jacoco.exec").getAbsolutePath
      val agentJavaOption = "-javaagent:%s=output=file,destfile=%s" format (agentFilePath, executionDataPath)
  
      addSetting(javaOptions in run += agentJavaOption)
    }
  }

  def uninstrument(implicit buildState: State) = {
    logger(buildState) info "Uninstrumenting the run tasks."

    addSetting(javaOptions in run <<= (javaOptions) { _ filter (_.contains("-javaagent:")) } )
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
  
  def doInJacocoDirectory(op: File => State)(implicit buildState: State) = {
    val jacocoDirectory = getSetting(outputDirectory)
    logger(buildState) debug ("jacoco target directory: " + jacocoDirectory)
    jacocoDirectory match {
      case Some(jacocoDirectory) => op(jacocoDirectory)
      case None => {
        logger(buildState) error "JaCoCo target directory undefined."
        buildState.fail
      }
    }
  }
  
  def extractedState(implicit state: State) = Project extract state
  def extractedSettings(implicit state: State) = extractedState.structure.data
  def addSetting(setting: Project.Setting[_])(implicit state: State) = extractedState.append(Seq(setting), state)
  def getSetting[T](setting: SettingKey[T])(implicit state: State) = 
    setting in (extractedState.currentRef, Config) get extractedSettings
}