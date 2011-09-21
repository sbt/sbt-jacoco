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

trait Utils extends Keys {
  
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