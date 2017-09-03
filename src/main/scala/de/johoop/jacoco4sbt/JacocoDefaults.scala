package de.johoop.jacoco4sbt

import sbt.Keys._
import sbt._

private[jacoco4sbt] object JacocoDefaults extends Reporting with Keys {
  val settings = Seq(
    outputDirectory := crossTarget.value / "jacoco",
    aggregateReportDirectory := outputDirectory.value / "aggregate",
    reportFormats := Seq(ScalaHTMLReport()),
    reportTitle := "Jacoco Coverage Report",
    aggregateReportTitle := "Jacoco Aggregate Coverage Report",
    sourceTabWidth := 2,
    sourceEncoding := "utf-8",
    thresholds := Thresholds(),
    aggregateThresholds := Thresholds(),
    includes := Seq("*"),
    excludes := Seq(),
    coveredSources := (sourceDirectories in Compile).value,
    instrumentedClassDirectory := outputDirectory.value / (classDirectory in Compile).value.getName,
    report := reportAction(
      outputDirectory.value,
      executionDataFile.value,
      reportFormats.value,
      reportTitle.value,
      coveredSources.value,
      classesToCover.value,
      sourceEncoding.value,
      sourceTabWidth.value,
      thresholds.value,
      streams.value
    ),
    aggregateReport := aggregateReportAction(
      aggregateReportDirectory.value,
      aggregateExecutionDataFiles.value,
      reportFormats.value,
      aggregateReportTitle.value,
      aggregateCoveredSources.value,
      aggregateClassesToCover.value,
      sourceEncoding.value,
      sourceTabWidth.value,
      aggregateThresholds.value,
      streams.value
    ),
    clean := outputDirectory map (dir => if (dir.exists) IO delete dir.listFiles)
  )
}
