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
import org.jacoco.core.runtime.LoggerRuntime

object JacocoPlugin extends Plugin {
  object jacoco extends Commands with Keys {

    def reportAction(jacocoDirectory: File, reportFormats: Seq[FormattedReport], reportTitle: String,
        sourceDirectories: Seq[File], classDirectories: Seq[File], sourceEncoding: String, tabWidth: Int) = {

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

    val instrumentationHooks = Seq(Compile, Runtime, Test) flatMap { config =>
      Seq(instrumentedClassDirectory in config <<= (outputDirectory, classDirectory in config) (_ / _.getName),
          products in config <<= (products in config, instrumentedClassDirectory in config, isInstrumented, streams) map { 
            instrumentAction(_, _, _, _) }) 
    }
    
    val settings : Seq[Setting[_]] = instrumentationHooks ++ Seq(
      commands += jacocoCommand,
      ivyConfigurations += Config,
      
      outputDirectory <<= (crossTarget) { _ / "jacoco" },
      reportFormats := Seq(HTMLReport()),
      reportTitle := "Jacoco Coverage Report",
      sourceTabWidth := 2,
      sourceEncoding := "utf-8",
      
      isInstrumented := false,

      combinedClassDirectories <<= (classDirectory in Compile) map (Seq(_)),
      sources in Config <<= (sourceDirectories in Compile) map identity,
      
      report <<= (outputDirectory, reportFormats, reportTitle, sources in Config, combinedClassDirectories, 
          sourceEncoding, sourceTabWidth) map reportAction)       
  }
}
