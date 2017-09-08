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

import sbt.Keys._
import sbt._

private[jacoco] object JacocoDefaults extends Reporting with CommonKeys {
  val settings = Seq(
    jacocoOutputDirectory := crossTarget.value / "jacoco",
    jacocoAggregateReportDirectory := jacocoOutputDirectory.value / "aggregate",
    jacocoSourceSettings := JacocoSourceSettings(),
    jacocoReportSettings := JacocoReportSettings(),
    jacocoAggregateReportSettings := JacocoReportSettings(title = "Jacoco Aggregate Coverage Report"),
    jacocoIncludes := Seq("*"),
    jacocoExcludes := Seq(),
    coveredSources := (sourceDirectories in Compile).value,
    jacocoInstrumentedDirectory := jacocoOutputDirectory.value / (classDirectory in Compile).value.getName,
    jacocoReport := reportAction(
      jacocoOutputDirectory.value,
      jacocoDataFile.value,
      jacocoReportSettings.value,
      coveredSources.value,
      classesToCover.value,
      jacocoSourceSettings.value,
      streams.value
    ),
    jacocoAggregateReport := aggregateReportAction(
      jacocoAggregateReportDirectory.value,
      aggregateExecutionDataFiles.value,
      jacocoAggregateReportSettings.value,
      aggregateCoveredSources.value,
      aggregateClassesToCover.value,
      jacocoSourceSettings.value,
      streams.value
    ),
    clean := jacocoOutputDirectory map (dir => if (dir.exists) IO delete dir.listFiles)
  )
}
