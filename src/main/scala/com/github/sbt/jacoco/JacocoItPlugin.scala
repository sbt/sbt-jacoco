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

import com.github.sbt.jacoco.build.BuildInfo
import com.github.sbt.jacoco.data.ExecutionDataUtils
import com.github.sbt.jacoco.report.{JacocoReportSettings, ReportUtils}
import sbt.Keys._
import sbt.plugins.JvmPlugin
import sbt.{Def, _}

object JacocoItPlugin extends BaseJacocoPlugin {

  object autoImport {
    lazy val jacocoMergeData: TaskKey[Unit] = taskKey[Unit]("Merges all '*.exec' files into a single data file.")

    lazy val jacocoMergedDataFile: SettingKey[File] =
      settingKey[File]("Execution data file contain unit test and integration test data.")

    lazy val jacocoMergedReportSettings: SettingKey[JacocoReportSettings] =
      settingKey[JacocoReportSettings]("todo")

    lazy val jacocoAutoMerge: SettingKey[Boolean] =
      settingKey[Boolean]("Indication whether to merge the unittest and integration test reports. Defaults to true.")

    lazy val jacocoMergedReport: TaskKey[Unit] =
      taskKey[Unit]("generates a merged report")
  }

  import autoImport._ // scalastyle:ignore import.grouping

  override def requires: Plugins = JvmPlugin && JacocoPlugin

  protected lazy val srcConfig: Configuration = IntegrationTest

  private val autoMerge: Def.Initialize[Task[Unit]] = Def.taskDyn {
    if (jacocoAutoMerge.value) {
      Def.task {
        jacocoMergedReport.value
      }
    } else {
      Def.task {}
    }
  }

  override protected def dependencyValues: Seq[Setting[_]] = Seq(
    libraryDependencies ++= {
      if ((fork in Test).value || (fork in IntegrationTest).value) {
        // config is set to fork - need to add the jacoco agent to the classpath so it can process instrumentation
        Seq("org.jacoco" % "org.jacoco.agent" % BuildInfo.jacocoVersion % "test,it" classifier "runtime")
      } else {
        Nil
      }
    }
  )

  override def projectSettings: Seq[Setting[_]] =
    Defaults.itSettings ++
      super.projectSettings ++
      // move the test reports to a subdirectory to disambiguate
      Seq(jacocoReportDirectory := (jacocoDirectory in Test).value / "report" / "test") ++
      inConfig(IntegrationTest) {
        Seq(
          jacocoReportDirectory := jacocoDirectory.value / "report" / "it",
          jacocoDataFile := jacocoDataDirectory.value / "jacoco-it.exec",
          jacocoMergeData := ExecutionDataUtils.mergeExecutionData(
            Seq(
              (jacocoDataFile in Test).value,
              (jacocoDataFile in IntegrationTest).value
            ),
            (jacocoMergedDataFile in IntegrationTest).value,
            streams.value),
          jacocoAutoMerge := true,
          jacocoMergedDataFile := jacocoDataDirectory.value / "jacoco-merged.exec",
          jacocoReportSettings := JacocoReportSettings("Jacoco Integration Test Coverage Report"),
          jacocoMergedReportSettings := JacocoReportSettings("Jacoco Merged Coverage Report"),
          jacocoMergedReport := Def
            .task(
              ReportUtils.generateReport(
                jacocoDirectory.value / "report" / "merged",
                jacocoMergedDataFile.value,
                jacocoMergedReportSettings.value,
                coveredSources.value,
                classesToCover.value,
                jacocoSourceSettings.value,
                streams.value
              )
            )
            .dependsOn(jacocoMergeData)
            .value,
          jacocoReport := Def.sequential(jacocoReport, autoMerge).value
        )
      }
}
