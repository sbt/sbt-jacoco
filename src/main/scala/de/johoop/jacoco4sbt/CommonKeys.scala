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

//import de.johoop.jacoco4sbt.report.JacocoSourceSettings
import de.johoop.jacoco4sbt.report.{JacocoReportSettings, JacocoSourceSettings}
import de.johoop.jacoco4sbt.report.formats.JacocoReportFormat
import sbt._

private[jacoco4sbt] trait CommonKeys {
  val jacoco: TaskKey[Unit] = taskKey[Unit]("Executes the tests and creates a JaCoCo coverage report.")

  val jacocoCheck: TaskKey[Unit] = taskKey[Unit]("Executes the tests and saves the execution data in 'jacoco.exec'.")
  val jacocoReport: TaskKey[Unit] =
    taskKey[Unit]("Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")

  val jacocoAggregate: TaskKey[Unit] = taskKey[Unit](
    "Executes the tests and creates a JaCoCo coverage report as well as an aggregated report which merges all sub-projects.")

  val jacocoAggregateReport: TaskKey[Unit] = taskKey[Unit]("Generates an aggregated JaCoCo report.")

  private[jacoco4sbt] val coveredSources: TaskKey[Seq[File]] = taskKey[Seq[File]]("Covered Sources.")

  private[jacoco4sbt] val aggregateCoveredSources: TaskKey[Seq[File]] =
    taskKey[Seq[File]]("Covered Sources across all aggregated projects.")
  private[jacoco4sbt] val classesToCover: TaskKey[Seq[File]] =
    taskKey[Seq[File]]("compiled classes (filtered by includes and excludes) that will be covered")
  private[jacoco4sbt] val aggregateClassesToCover: TaskKey[Seq[File]] = taskKey[Seq[File]](
    "compiled classes (filtered by includes and excludes) that will be covered across all aggregated project")

  private[jacoco4sbt] val aggregateExecutionDataFiles: TaskKey[Seq[File]] =
    taskKey[Seq[File]]("All execution data output files for aggregated modules.")

  val outputDirectory: SettingKey[File] =
    settingKey[File]("Where JaCoCo should store its execution data and reports.")
  val aggregateReportDirectory: SettingKey[File] =
    settingKey[File]("Where JaCoCo should store its aggregate reports.")
  val executionDataFile: SettingKey[File] = settingKey[File]("Execution data output file.")

  val jacocoSourceSettings: SettingKey[JacocoSourceSettings] =
    settingKey[JacocoSourceSettings]("Input source code settings (encoding etc) for reporting.")

  val jacocoReportSettings: SettingKey[JacocoReportSettings] =
    settingKey[JacocoReportSettings]("Settings for JaCoCo report (format, title etc)")
  val jacocoAggregateReportSettings: SettingKey[JacocoReportSettings] =
    settingKey[JacocoReportSettings]("Settings for aggregate JaCoCo report (format, title etc)")

  val includes: SettingKey[Seq[String]] = settingKey[Seq[String]](
    "glob patterns specifying which classes to cover; excludes override includes; default: all classes included")

  val excludes: SettingKey[Seq[String]] = settingKey[Seq[String]](
    "glob patterns specifying which classes not to cover; excludes override includes; default: no classes excluded")

  val instrumentedClassDirectory: SettingKey[File] =
    settingKey[File]("Directory containing the instrumented classes.")

  /**
    * Example - in build.sbt add
    * jacoco.thresholds in jacoco.Config := Thresholds(instruction = 35, method = 40, branch = 30, complexity = 35, line = 50, clazz = 40)
    */
  val thresholds: SettingKey[Thresholds] = settingKey[Thresholds]("Required coverage ratios.")
  val aggregateThresholds: SettingKey[Thresholds] =
    settingKey[Thresholds]("Required coverage ratios for the aggregate report.")

  // type aliases for auto import
  val JacocoSourceSettings: report.JacocoSourceSettings.type = report.JacocoSourceSettings
  val JacocoReportSettings: report.JacocoReportSettings.type = report.JacocoReportSettings
  val JacocoReportFormats: report.JacocoReportFormats.type = report.JacocoReportFormats
}
