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

package com.github.sbt.jacoco

import java.io.File

import com.github.sbt.jacoco.JacocoPluginCompat.*
import com.github.sbt.jacoco.build.BuildInfo
import com.github.sbt.jacoco.data.{ExecutionDataUtils, InstrumentationUtils, ProjectData}
import com.github.sbt.jacoco.report.ReportUtils
import sbt.Keys.*
import sbt.plugins.JvmPlugin
import sbt.*

private[jacoco] abstract class BaseJacocoPlugin extends AutoPlugin with JacocoKeys {
  override def requires: Plugins = JvmPlugin

  protected def srcConfig: Configuration

  override def projectSettings: Seq[Setting[?]] =
    dependencyValues ++
      unscopedSettingValues ++
      inConfig(srcConfig)(scopedSettingValues ++ taskValues)

  protected def dependencyValues: Seq[Setting[?]] = Seq(
    libraryDependencies ++= {
      if ((Test / fork).value) {
        // config is set to fork - need to add the jacoco agent to the classpath so it can process instrumentation
        Seq("org.jacoco" % "org.jacoco.agent" % BuildInfo.jacocoVersion % Test classifier "runtime")
      } else {
        Nil
      }
    }
  )

  private def unscopedSettingValues = Seq(
    jacocoDirectory := crossTarget.value / "jacoco",
    jacocoReportDirectory := jacocoDirectory.value / "report",
    jacocoSourceSettings := JacocoSourceSettings(),
    jacocoReportSettings := JacocoReportSettings(),
    jacocoAggregateReportSettings := JacocoReportSettings(title = "Jacoco Aggregate Coverage Report"),
    jacocoIncludes := Seq("*"),
    jacocoExcludes := Seq(),
    jacocoInstrumentedDirectory := jacocoDirectory.value / "instrumented-classes",
    jacocoInstrumentationIncludes := Seq("*"),
    jacocoInstrumentationExcludes := Seq(),
    jacocoDataFile := jacocoDataDirectory.value / "jacoco.exec"
  )

  private def scopedSettingValues = Seq(
    javaOptions ++= {
      val dest = jacocoDataFile.value

      if (fork.value) {
        Seq(
          s"-Djacoco-agent.destfile=${dest.absolutePath}"
        )
      } else {
        Nil
      }
    }
  )

  private def taskValues = Seq(
    jacoco := (jacocoReport dependsOn jacocoCheck).value,
    jacocoAggregate := (jacocoAggregateReport dependsOn submoduleCoverTasks).value,
    jacocoCheck := Def
      .task(
        ExecutionDataUtils
          .saveRuntimeData(projectData(thisProject.value), jacocoDataFile.value, fork.value, streams.value)
      )
      .dependsOn(JacocoPluginCompat.testFull)
      .value,
    jacocoReport := ReportUtils.generateReport(
      jacocoReportDirectory.value,
      jacocoDataFile.value,
      jacocoReportSettings.value,
      coveredSources.value,
      classesToCover.value,
      jacocoSourceSettings.value,
      streams.value
    ),
    jacocoAggregateReport := ReportUtils.generateAggregateReport(
      jacocoReportDirectory.value / "aggregate",
      aggregateExecutionDataFiles.value,
      jacocoAggregateReportSettings.value,
      aggregateCoveredSources.value,
      aggregateClassesToCover.value,
      jacocoSourceSettings.value,
      streams.value
    ),
    clean := jacocoDirectory.map(dir => if (dir.exists) IO delete dir.listFiles).value,
    fullClasspath := Def.uncached(
      JacocoPluginCompat.convertClasspath(
        InstrumentationUtils.instrumentClasses(
          (Compile / products).value,
          filterClassesToInstrument(
            (Compile / products).value,
            (srcConfig / jacocoInstrumentationIncludes).value,
            (srcConfig / jacocoInstrumentationExcludes).value
          ),
          JacocoPluginCompat.fromClasspath((srcConfig / fullClasspath).value),
          jacocoInstrumentedDirectory.value,
          update.value,
          fork.value,
          projectData(thisProject.value),
          streams.value
        )
      )
    ),
    definedTests := (srcConfig / definedTests).value,
    definedTestNames := (srcConfig / definedTestNames).value
  )

  private def filterClassesToInstrument(products: Seq[File], incl: Seq[String], excl: Seq[String]) = {
    val inclFilters = incl map GlobFilter.apply
    val exclFilters = excl map GlobFilter.apply

    products.flatMap { product =>
      (PathFinder(product) ** new FileFilter {
        def accept(f: File): Boolean =
          IO.relativize(product, f) match {
            case Some(file) if !f.isDirectory && file.endsWith(".class") =>
              val name = toClassName(file)
              inclFilters.exists(_ accept name) && !exclFilters.exists(_ accept name)
            case _ => false
          }
      }).get()
    }
  }

  private def filterClassesToCover(classes: File, incl: Seq[String], excl: Seq[String]) = {
    val inclFilters = incl map GlobFilter.apply
    val exclFilters = excl map GlobFilter.apply

    (PathFinder(classes) ** new FileFilter {
      def accept(f: File): Boolean = IO.relativize(classes, f) match {
        case Some(file) if !f.isDirectory && file.endsWith(".class") =>
          val name = toClassName(file)
          inclFilters.exists(_ accept name) && !exclFilters.exists(_ accept name)
        case _ => false
      }
    }).get()
  }

  private def toClassName(entry: String): String =
    entry.stripSuffix(".class").replace(File.separatorChar, '.')

  protected lazy val submoduleSettingsTask: Def.Initialize[Task[(Seq[File], Option[File], Option[File])]] = Def.task {
    (classesToCover.value, (Compile / sourceDirectory).?.value, (srcConfig / jacocoDataFile).?.value)
  }

  protected lazy val submoduleSettings: Def.Initialize[Task[Seq[(Seq[File], Option[File], Option[File])]]] =
    submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject), inConfigurations(Compile, srcConfig)))

  protected lazy val aggregateCoveredSources: Def.Initialize[Task[Seq[File]]] = Def.task {
    submoduleSettings.value.flatMap(_._2).distinct
  }

  protected lazy val classesToCover: Def.Initialize[Task[Seq[File]]] = Def.task {
    filterClassesToCover(
      (Compile / classDirectory).value,
      (srcConfig / jacocoIncludes).value,
      (srcConfig / jacocoExcludes).value
    )
  }

  protected lazy val aggregateClassesToCover: Def.Initialize[Task[Seq[File]]] = Def.task {
    submoduleSettings.value.flatMap(_._1).distinct
  }

  protected lazy val aggregateExecutionDataFiles: Def.Initialize[Task[Seq[File]]] = Def.task {
    submoduleSettings.value.flatMap(_._3).distinct
  }

  protected lazy val coveredSources: Def.Initialize[Task[Seq[File]]] = Def.task {
    (Compile / sourceDirectories).value
  }

  protected lazy val jacocoDataDirectory: Def.Initialize[File] = Def.setting {
    jacocoDirectory.value / "data"
  }

  protected lazy val submoduleCoverTasks: Def.Initialize[Task[Seq[Unit]]] = {
    (srcConfig / jacoco).all(ScopeFilter(inAggregates(ThisProject)))
  }
}
