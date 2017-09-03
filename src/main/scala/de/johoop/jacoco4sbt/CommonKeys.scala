/*
 * This file is part of jacoco4sbt.
 *
 * Copyright (c) Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package de.johoop.jacoco4sbt

import sbt._

private[jacoco4sbt] trait CommonKeys {
  lazy val outputDirectory =
    SettingKey[File]("output-directory", "Where JaCoCo should store its execution data and reports.")
  lazy val aggregateReportDirectory =
    SettingKey[File]("aggregate-report-directory", "Where JaCoCo should store its aggregate reports.")
  lazy val executionDataFile = SettingKey[File]("execution-data-file", "Execution data output file.")
  lazy val aggregateExecutionDataFiles =
    TaskKey[Seq[File]]("aggregate-execution-data-files", "All execution data output files for aggregated modules.")
  lazy val reportTitle = SettingKey[String]("report-title", "Title of the JacoCo report to generate.")
  lazy val aggregateReportTitle =
    SettingKey[String]("aggregate-report-title", "Title of the JacoCo aggregate report to generate.")
  lazy val reportFormats = SettingKey[Seq[FormattedReport]](
    "report-formats",
    "Set of formats (XML, CSV, HTML) of the JaCoCo reports to generate.")

  lazy val sourceTabWidth =
    SettingKey[Int]("source-tab-width", "Tab width of the sources to display in the JaCoCo reports.")
  lazy val sourceEncoding =
    SettingKey[String]("source-encoding", "Encoding of the source files (for JaCoCo reporting).")

  lazy val coveredSources = TaskKey[Seq[File]]("covered-sources", "Covered Sources.")
  lazy val aggregateCoveredSources =
    TaskKey[Seq[File]]("aggregate-covered-sources", "Covered Sources across all aggregated projects.")
  lazy val classesToCover =
    TaskKey[Seq[File]]("classes-to-cover", "compiled classes (filtered by includes and excludes) that will be covered")
  lazy val aggregateClassesToCover = TaskKey[Seq[File]](
    "aggregate-classes-to-cover",
    "compiled classes (filtered by includes and excludes) that will be covered across all aggregated project")

  lazy val includes = SettingKey[Seq[String]](
    "jacoco-includes",
    "glob patterns specifying which classes to cover; excludes override includes; default: all classes included")

  lazy val excludes = SettingKey[Seq[String]](
    "jacoco-excludes",
    "glob patterns specifying which classes not to cover; excludes override includes; default: no classes excluded")

  lazy val instrumentedClassDirectory =
    SettingKey[File]("instrumented-class-directory", "Directory containing the instrumented classes.")

  /**
    * Example - in build.sbt add
    * jacoco.thresholds in jacoco.Config := Thresholds(instruction = 35, method = 40, branch = 30, complexity = 35, line = 50, clazz = 40)
    */
  lazy val thresholds = SettingKey[Thresholds]("thresholds", "Required coverage ratios.")
  lazy val aggregateThresholds =
    SettingKey[Thresholds]("aggregate-thresholds", "Required coverage ratios for the aggregate report.")

  lazy val report =
    TaskKey[Unit]("report", "Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")
  lazy val aggregateReport = TaskKey[Unit]("aggregate-report", "Generates an aggregated JaCoCo report.")

  lazy val cover = TaskKey[Unit]("cover", "Executes the tests and creates a JaCoCo coverage report.")
  lazy val aggregateCover = TaskKey[Unit](
    "aggregate-cover",
    "Executes the tests and creates a JaCoCo coverage report as well as an aggregated report which merges all sub-projects.")

  lazy val check = TaskKey[Unit]("check", "Executes the tests and saves the execution data in 'jacoco.exec'.")
  lazy val clean = TaskKey[Unit]("clean", "Cleaning JaCoCo's output-directory.")
}
