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

import de.johoop.jacoco4sbt.report.JacocoSourceSettings
import sbt.Keys._
import sbt._

private[jacoco4sbt] object JacocoDefaults extends Reporting with CommonKeys {
  val settings = Seq(
    outputDirectory := crossTarget.value / "jacoco",
    aggregateReportDirectory := outputDirectory.value / "aggregate",
    reportFormats := Seq(ScalaHTMLReport()),
    reportTitle := "Jacoco Coverage Report",
    aggregateReportTitle := "Jacoco Aggregate Coverage Report",
    jacocoSourceSettings := JacocoSourceSettings(),
    thresholds := Thresholds(),
    aggregateThresholds := Thresholds(),
    includes := Seq("*"),
    excludes := Seq(),
    coveredSources := (sourceDirectories in Compile).value,
    instrumentedClassDirectory := outputDirectory.value / (classDirectory in Compile).value.getName,
    jacocoReport := reportAction(
      outputDirectory.value,
      executionDataFile.value,
      reportFormats.value,
      reportTitle.value,
      coveredSources.value,
      classesToCover.value,
      jacocoSourceSettings.value,
      thresholds.value,
      streams.value
    ),
    jacocoAggregateReport := aggregateReportAction(
      aggregateReportDirectory.value,
      aggregateExecutionDataFiles.value,
      reportFormats.value,
      aggregateReportTitle.value,
      aggregateCoveredSources.value,
      aggregateClassesToCover.value,
      jacocoSourceSettings.value,
      aggregateThresholds.value,
      streams.value
    ),
    clean := outputDirectory map (dir => if (dir.exists) IO delete dir.listFiles)
  )
}
