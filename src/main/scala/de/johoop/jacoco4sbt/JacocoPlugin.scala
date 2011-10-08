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

object JacocoPlugin extends Plugin {
  object jacoco extends Reporting with Instrumentation with Keys {

    val settings = Seq(ivyConfigurations += Config) ++
      inConfig(Config)(Defaults.testTasks ++ Seq( 

      outputDirectory <<= (crossTarget) { _ / "jacoco" },
      reportFormats := Seq(HTMLReport()),
      reportTitle := "Jacoco Coverage Report",
      sourceTabWidth := 2,
      sourceEncoding := "utf-8",
      
      classesToCover <<= (classDirectory in Compile) map (Seq(_)),
      coveredSources <<= (sourceDirectories in Compile) map identity,
      
      instrumentedClassDirectory <<= (outputDirectory, classDirectory in Compile) (_ / _.getName),

      report <<= (outputDirectory, reportFormats, reportTitle, coveredSources, classesToCover, 
          sourceEncoding, sourceTabWidth, streams) map reportAction,

      definedTests <<= definedTests in Test,
      fullClasspath <<= (products in Compile, fullClasspath in Test, instrumentedClassDirectory, streams) map instrumentAction,
      cover <<= report.dependsOn(test)))
  }
}
