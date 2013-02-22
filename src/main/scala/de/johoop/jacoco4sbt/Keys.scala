/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011, 2012 Joachim Hofer & contributors
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
  lazy val Config = config("jacoco") extend(Test) hide

  lazy val outputDirectory = SettingKey[File]("output-directory", "Where JaCoCo should store its execution data and reports.")
  lazy val reportTitle = SettingKey[String]("report-title", "Title of the JacoCo report to generate.")
  lazy val reportFormats = SettingKey[Seq[FormattedReport]]("report-formats", "Set of formats (XML, CSV, HTML) of the JaCoCo reports to generate.")
  lazy val sourceTabWidth = SettingKey[Int]("source-tab-width", "Tab width of the sources to display in the JaCoCo reports.")
  lazy val sourceEncoding = SettingKey[String]("source-encoding", "Encoding of the source files (for JaCoCo reporting).")

  lazy val coveredSources = TaskKey[Seq[File]]("covered-sources", "Covered Sources.")
  lazy val classesToCover = TaskKey[Seq[File]]("classes-to-cover", "compiled classes (filtered by includes and excludes) that will be covered")
  
  lazy val includes = SettingKey[Seq[String]]("jacoco-includes", "glob patterns specifying which classes to cover; excludes override includes; default: all classes included")
  
  lazy val excludes = SettingKey[Seq[String]]("jacoco-excludes", "glob patterns specifying which classes not to cover; excludes override includes; default: no classes excluded")

  lazy val instrumentedClassDirectory = SettingKey[File]("instrumented-class-directory", "Directory containing the instrumented classes.")
  
  lazy val report = TaskKey[Unit]("report", "Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")
  lazy val cover = TaskKey[Unit]("cover", "Executes the tests and creates a JaCoCo coverage report.")

  lazy val check = TaskKey[Unit]("check", "Executes the tests and saves the execution data in 'jacoco.exec'.")
  lazy val clean = TaskKey[Unit]("clean", "Cleaning JaCoCo's output-directory.")
}

trait IntegrationTestKeys extends Keys {

  lazy val IntegrationTestConfig = config("it-jacoco") extend(IntegrationTest) hide

  lazy val merge = TaskKey[Unit]("merge", "Merges all '*.exec' files into a single data file.")
  lazy val mergeReports = SettingKey[Boolean]("merge-reports", "Indication whether to merge the unittest and integration test reports. Defaults to true.")
}
