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
import Keys._
import java.io.File

import de.johoop.jacoco4sbt.build.BuildInfo
import sbt.plugins.JvmPlugin

object JacocoPlugin extends AutoPlugin {

  private object JacocoDefaults extends Reporting with Keys {
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

  private def filterClassesToCover(classes: File, incl: Seq[String], excl: Seq[String]) = {
    val inclFilters = incl map GlobFilter.apply
    val exclFilters = excl map GlobFilter.apply

    PathFinder(classes) ** new FileFilter {
      def accept(f: File) = IO.relativize(classes, f) match {
        case Some(file) if !f.isDirectory && file.endsWith(".class") =>
          val name = toClassName(file)
          inclFilters.exists(_ accept name) && !exclFilters.exists(_ accept name)
        case _ => false
      }
    } get
  }

  private def toClassName(entry: String): String =
    entry.stripSuffix(ClassExt).replace('/', '.')

  private val ClassExt = ".class"

  override def requires = JvmPlugin

  object autoImport {
    val jacoco = JacocoPlugin.jacoco
    val itJacoco = JacocoPlugin.itJacoco
  }
  object jacoco extends SharedSettings with Reporting with SavingData with Instrumentation with Keys {
    lazy val srcConfig = Test

    override def settings =
      super.settings ++ Seq((executionDataFile in Config) := (outputDirectory in Config).value / "jacoco.exec")
  }

  object itJacoco
      extends SharedSettings
      with Reporting
      with Merging
      with SavingData
      with Instrumentation
      with IntegrationTestKeys {
    lazy val srcConfig = IntegrationTest

    lazy val conditionalMerge = Def.task {
      conditionalMergeAction(
        (outputDirectory in Config).value,
        (outputDirectory in jacoco.Config).value,
        streams.value,
        mergeReports.value)
    }
    lazy val forceMerge = Def.task {
      mergeAction((outputDirectory in Config).value, (outputDirectory in jacoco.Config).value, streams.value)
    }

    override def settings =
      super.settings ++ Seq(
        report in Config := ((report in Config) dependsOn conditionalMerge).value,
        merge := forceMerge.value,
        mergeReports := true,
        (executionDataFile in Config) := (outputDirectory in Config).value / "jacoco-merged.exec"
      )
  }

  trait SharedSettings { _: Reporting with SavingData with Instrumentation with Keys =>

    lazy val submoduleSettingsTask = Def.task {
      ((classesToCover in Config ?).value, (sourceDirectory in Compile ?).value, (executionDataFile in Config ?).value)
    }

    val submoduleSettings =
      submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject), inConfigurations(Compile, Config)))

    val submoduleCoverTasks = (cover in Config).all(ScopeFilter(inAggregates(ThisProject)))

    def srcConfig: Configuration

    def settings =
      Seq(
        ivyConfigurations += Config,
        libraryDependencies +=
          "org.jacoco" % "org.jacoco.agent" % BuildInfo.jacocoVersion % "jacoco" artifacts Artifact(
            "org.jacoco.agent",
            "jar",
            "jar")
      ) ++ inConfig(Config)(
        Defaults.testSettings ++ JacocoDefaults.settings ++ Seq(
          classesToCover := filterClassesToCover((classDirectory in Compile).value, includes.value, excludes.value),
          aggregateClassesToCover := submoduleSettings.value.flatMap(_._1).flatten.distinct,
          aggregateCoveredSources := submoduleSettings.value.flatMap(_._2).distinct,
          aggregateExecutionDataFiles := submoduleSettings.value.flatMap(_._3).distinct,
          fullClasspath := instrumentAction(
            (products in Compile).value,
            (fullClasspath in srcConfig).value,
            instrumentedClassDirectory.value,
            update.value,
            fork.value,
            streams.value),
          javaOptions ++= {
            val dir = outputDirectory.value
            if (fork.value) Seq(s"-Djacoco-agent.destfile=${dir / "jacoco.exec" absolutePath}") else Seq()
          },
          outputDirectory in Config := crossTarget.value / Config.name,
          definedTests := (definedTests in srcConfig).value,
          definedTestNames := (definedTestNames in srcConfig).value,
          cover := (report dependsOn check).value,
          aggregateCover := (aggregateReport dependsOn submoduleCoverTasks).value,
          check := Def.task(saveDataAction(executionDataFile.value, fork.value, streams.value)).dependsOn(test).value
        ))
  }
}
