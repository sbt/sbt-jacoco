/*
 * This file is part of sbt-jacoco.
 *
 * Copyright (c) Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.scalasbt.jacoco

import org.scalasbt.jacoco.report.{JacocoReportSettings, JacocoSourceSettings}
import sbt._

// scalastyle:off line.size.limit
private[jacoco] trait CommonKeys {
  val jacoco: TaskKey[Unit] = taskKey[Unit]("Executes the tests and creates a JaCoCo coverage report.")

  val jacocoCheck: TaskKey[Unit] = taskKey[Unit]("Executes the tests and saves the execution data in 'jacoco.exec'.")
  val jacocoReport: TaskKey[Unit] =
    taskKey[Unit]("Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")

  val jacocoAggregate: TaskKey[Unit] = taskKey[Unit](
    "Executes the tests and creates a JaCoCo coverage report as well as an aggregated report which merges all sub-projects.")

  val jacocoAggregateReport: TaskKey[Unit] = taskKey[Unit]("Generates an aggregated JaCoCo report.")

  val jacocoOutputDirectory: SettingKey[File] =
    settingKey[File]("Where JaCoCo should store its execution data and reports.")
  val jacocoAggregateReportDirectory: SettingKey[File] =
    settingKey[File]("Where JaCoCo should store its aggregate reports.")
  val jacocoDataFile: SettingKey[File] = settingKey[File]("Execution data output file.")

  val jacocoSourceSettings: SettingKey[JacocoSourceSettings] =
    settingKey[JacocoSourceSettings]("Input source code settings (encoding etc) for reporting.")

  val jacocoReportSettings: SettingKey[JacocoReportSettings] =
    settingKey[JacocoReportSettings]("Settings for JaCoCo report (format, title etc)")
  val jacocoAggregateReportSettings: SettingKey[JacocoReportSettings] =
    settingKey[JacocoReportSettings]("Settings for aggregate JaCoCo report (format, title etc)")

  val jacocoIncludes: SettingKey[Seq[String]] = settingKey[Seq[String]](
    "glob patterns specifying which classes to cover; excludes override includes; default: all classes included")
  val jacocoExcludes: SettingKey[Seq[String]] = settingKey[Seq[String]](
    "glob patterns specifying which classes not to cover; excludes override includes; default: no classes excluded")

  val jacocoInstrumentedDirectory: SettingKey[File] =
    settingKey[File]("Directory containing the instrumented classes.")

  // type aliases for auto import
  val JacocoThresholds: report.JacocoThresholds.type = report.JacocoThresholds
  val JacocoSourceSettings: report.JacocoSourceSettings.type = report.JacocoSourceSettings
  val JacocoReportSettings: report.JacocoReportSettings.type = report.JacocoReportSettings
  val JacocoReportFormats: report.JacocoReportFormats.type = report.JacocoReportFormats

  private[jacoco] val coveredSources: TaskKey[Seq[File]] = taskKey[Seq[File]]("Covered Sources.")

  private[jacoco] val aggregateCoveredSources: TaskKey[Seq[File]] =
    taskKey[Seq[File]]("Covered Sources across all aggregated projects.")
  private[jacoco] val classesToCover: TaskKey[Seq[File]] =
    taskKey[Seq[File]]("compiled classes (filtered by includes and excludes) that will be covered")
  private[jacoco] val aggregateClassesToCover: TaskKey[Seq[File]] = taskKey[Seq[File]](
    "compiled classes (filtered by includes and excludes) that will be covered across all aggregated project")

  private[jacoco] val aggregateExecutionDataFiles: TaskKey[Seq[File]] =
    taskKey[Seq[File]]("All execution data output files for aggregated modules.")
}
