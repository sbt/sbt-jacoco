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
import complete.Parsers._
import CommandSupport.logger

trait Commands extends Keys {
  private lazy val Clean = "clean"
  private lazy val Instrument = "instrument"
  private lazy val Uninstrument = "uninstrument"
  private lazy val Report = "report"

  private lazy val grammar = 
    Space ~> Instrument | Space ~> Uninstrument | Space ~> Clean | Space ~> Report

  private[jacoco4sbt] lazy val jacocoCommand = Command("jacoco")(_ => grammar) { (buildState, arguments) =>

    implicit val implicitState = buildState

    arguments match {
      case Instrument => instrument 
      case Uninstrument => uninstrument
      case Clean => cleanUp
      case Report => report
    }
  }

  def instrument(implicit buildState: State) = {
    logger(buildState) info "Instrumenting the run tasks."

    doInJacocoDirectory { jacocoDirectory =>
      val agentFilePath = extractedState.evalTask(unpackJacocoAgent, buildState).getAbsolutePath
      val executionDataPath = (jacocoDirectory / "jacoco.exec").getAbsolutePath
      val agentJavaOption = "-javaagent:%s=output=file,destfile=%s" format (agentFilePath, executionDataPath)
  
      addSettings(Seq(
          javaOptions in run += agentJavaOption))
    }
  }

  def uninstrument(implicit buildState: State) = {
    logger(buildState) info "Uninstrumenting the run tasks."

    addSettings(Seq(javaOptions in run <<= (javaOptions) { _ filter (_.contains("-javaagent:")) } ))
  }

  def cleanUp(implicit buildState: State) = {
    logger(buildState) info "Cleaning JaCoCo directory."
    
    doInJacocoDirectory { jacocoDirectory => 
      IO delete jacocoDirectory
      buildState
    }
  }
  
  def report(implicit buildState: State) = {
    logger(buildState) info "Generating JaCoCo coverage report."

    Project.evaluateTask(jacocoReport in Config, buildState)

    buildState
  }
  
  def doInJacocoDirectory(op: File => State)(implicit buildState: State) = {
    val jacocoDirectory = outputDirectory in (extractedState.currentRef, Config) get extractedSettings
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
  def addSettings(settings: Seq[Project.Setting[_]])(implicit state: State) = extractedState.append(settings, state)

}