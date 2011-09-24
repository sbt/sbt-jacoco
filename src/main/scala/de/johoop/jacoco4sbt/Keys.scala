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
import org.jacoco.core.runtime.IRuntime

trait Keys {
  lazy val Config = config("jacoco") hide

  lazy val outputDirectory = SettingKey[File]("jacoco-output-directory", "Where JaCoCo should store its execution data and reports.")
  lazy val reportTitle = SettingKey[String]("jacoco-report-title", "Title of the JacoCo report to generate.")
  lazy val reportFormats = SettingKey[Seq[FormattedReport]]("jacoco-report-formats", "Set of formats (XML, CSV, HTML) of the JaCoCo reports to generate.")
  lazy val sourceTabWidth = SettingKey[Int]("jacoco-source-tab-width", "Tab width of the sources to display in the JaCoCo reports.")
  lazy val sourceEncoding = SettingKey[String]("jacoco-source-encoding", "Encoding of the source files (for JaCoCo reporting).")

  lazy val isInstrumented = SettingKey[Boolean]("jacoco-is-instrumented", "Whether the classes are currently instrumented for JaCoCo or not.")
  
  lazy val combinedClassDirectories = TaskKey[Seq[File]]("jacoco-combined-class-directories", "Combined Compile and Test class directories.")
  lazy val instrumentedClassDirectory = SettingKey[File]("jacoco-instrumented-class-directory", "Directory containing the instrumented classes.")
  
  lazy val report = TaskKey[Unit]("jacoco-report", "Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")
}
