/*
 * This file is part of jacoco4sbt.
 *
 * Copyright (c) 2011-2013 Joachim Hofer & contributors
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
import java.io.File
import inc.Locate

object JacocoPlugin extends Plugin {

  private object JacocoDefaults extends Reporting with Keys {
    val settings = Seq(
      outputDirectory := crossTarget.value / "jacoco",
      aggregateReportDirectory := outputDirectory.value / "aggregate",
      reportFormats := Seq(ScalaHTMLReport()),

      reportTitle := "Jacoco Coverage Report",
      aggregateReportTitle := "Jacoco Aggregate Coverage Report",
      sourceTabWidth := 2,
      sourceEncoding := "utf-8",

      thresholds:= Thresholds(),
      aggregateThresholds := Thresholds(),
      includes := Seq("*"),

      excludes := Seq(),

      coveredSources := (sourceDirectories in Compile).value,

      instrumentedClassDirectory := outputDirectory.value / (classDirectory in Compile).value.getName,

      report <<= (outputDirectory, executionDataFile, reportFormats, reportTitle, coveredSources, classesToCover,
        sourceEncoding, sourceTabWidth, thresholds, streams) map reportAction,

      aggregateReport <<= (aggregateReportDirectory, aggregateExecutionDataFiles, reportFormats, aggregateReportTitle, aggregateCoveredSources, aggregateClassesToCover,
        sourceEncoding, sourceTabWidth, aggregateThresholds, streams) map aggregateReportAction,

      clean <<= outputDirectory map (dir => if (dir.exists) IO delete dir.listFiles)
    )
  }

  private def filterClassesToCover(classes: File, incl: Seq[String], excl: Seq[String]) = {
    val inclFilters = incl map GlobFilter.apply
    val exclFilters = excl map GlobFilter.apply

    PathFinder(classes) ** new FileFilter {
      def accept(f: File) = IO.relativize(classes, f) match {
        case Some(file) if ! f.isDirectory && file.endsWith(".class") =>
          val name = Locate.toClassName(file)
          inclFilters.exists(_ accept name) && ! exclFilters.exists(_ accept name)
        case _ => false
      }
    } get
  }

  object jacoco extends SharedSettings with Reporting with SavingData with Instrumentation with Keys {
    lazy val srcConfig = Test

    override def settings = super.settings ++ Seq(
      executionDataFile := (outputDirectory in Config).value / "jacoco.exec")
  }

  object itJacoco extends SharedSettings with Reporting with Merging with SavingData with Instrumentation with IntegrationTestKeys {
    lazy val srcConfig = IntegrationTest

    lazy val conditionalMerge = (outputDirectory in Config, outputDirectory in jacoco.Config, streams, mergeReports) map conditionalMergeAction
    lazy val forceMerge = (outputDirectory in Config, outputDirectory in jacoco.Config, streams) map mergeAction

    override def settings = super.settings ++ Seq(
      report  in Config <<= (report  in Config) dependsOn conditionalMerge,
      merge <<= forceMerge,
      mergeReports := true,
      executionDataFile := (outputDirectory in Config).value / "jacoco-merged.exec")
  }

  trait SharedSettings { _: Reporting with SavingData with Instrumentation with Keys =>

    lazy val submoduleSettingsTask = Def.task {
      ((classesToCover in Config).value, (sourceDirectory in Compile).value, (executionDataFile in Config).value)
    }

    val submoduleSettings = submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject), inConfigurations(Compile, Config)))

    def srcConfig: Configuration

    def settings = Seq(ivyConfigurations += Config) ++ Seq(
      libraryDependencies +=
        "org.jacoco" % "org.jacoco.agent" % "0.7.5.201505241946" % "jacoco" artifacts(Artifact("org.jacoco.agent", "jar", "jar"))
    ) ++ inConfig(Config)(Defaults.testSettings ++ JacocoDefaults.settings ++ Seq(
      classesToCover <<= (classDirectory in Compile, includes, excludes) map filterClassesToCover,
      aggregateClassesToCover := submoduleSettings.value.flatMap(_._1).distinct,
      aggregateCoveredSources := submoduleSettings.value.map(_._2).distinct,
      aggregateExecutionDataFiles := submoduleSettings.value.map(_._3).distinct,
      fullClasspath <<= (products in Compile, fullClasspath in srcConfig, instrumentedClassDirectory, update, fork, streams) map instrumentAction,
      javaOptions <++= (fork, outputDirectory) map { (forked, out) =>
        if (forked) Seq(s"-Djacoco-agent.destfile=${out / "jacoco.exec" absolutePath}") else Seq()
      },

      outputDirectory in Config := crossTarget.value / Config.name,

      definedTests <<= definedTests in srcConfig,
      definedTestNames <<= definedTestNames in srcConfig,
      cover <<= report dependsOn check,
      aggregateCover <<= aggregateReport dependsOn (report dependsOn check),
      check <<= ((executionDataFile, fork, streams) map saveDataAction) dependsOn test))
  }
}
