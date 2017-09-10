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

import org.scalasbt.jacoco.JacocoPlugin.autoImport.Jacoco
import sbt.Keys._
import sbt._

object JacocoItPlugin extends BaseJacocoPlugin with Merging {

  object autoImport {
    lazy val ItJacoco: Configuration = config("it-jacoco").extend(IntegrationTest).hide

    lazy val jacocoMerge: TaskKey[Unit] = taskKey[Unit]("Merges all '*.exec' files into a single data file.")

    lazy val mergedExecutionDataFile: SettingKey[File] =
      settingKey[File]("Execution data file contain unit test and integration test data.")

    lazy val mergeReports: SettingKey[Boolean] =
      settingKey[Boolean]("Indication whether to merge the unittest and integration test reports. Defaults to true.")
  }

  import autoImport._

  override protected val pluginConfig: Configuration = ItJacoco
  lazy val srcConfig: Configuration = IntegrationTest

  lazy val conditionalMerge: Def.Initialize[Task[Unit]] = Def.task {
    conditionalMergeAction(
      (jacocoDataFile in Jacoco).value,
      (jacocoDataFile in ItJacoco).value,
      (mergedExecutionDataFile in ItJacoco).value,
      streams.value,
      mergeReports.value)
  }

  lazy val forceMerge: Def.Initialize[Task[Unit]] = Def.task {
    mergeAction(
      (jacocoDataFile in Jacoco).value,
      (jacocoDataFile in ItJacoco).value,
      (mergedExecutionDataFile in ItJacoco).value,
      streams.value)
  }

  override def projectSettings: Seq[Setting[_]] =
    super.projectSettings ++
      Seq(
        jacocoReport in ItJacoco := ((jacocoReport in ItJacoco) dependsOn conditionalMerge).value,
        jacocoMerge := forceMerge.value,
        mergeReports := true,
        (mergedExecutionDataFile in ItJacoco) := (jacocoOutputDirectory in ItJacoco).value / "jacoco-merged.exec",
        (jacocoReport in ItJacoco) := Reporting.reportAction(
          (jacocoOutputDirectory in ItJacoco).value,
          (mergedExecutionDataFile in ItJacoco).value,
          (jacocoReportSettings in ItJacoco).value,
          (coveredSources in ItJacoco).value,
          (classesToCover in ItJacoco).value,
          (jacocoSourceSettings in ItJacoco).value,
          (streams in ItJacoco).value
        ),
        jacocoReport in ItJacoco := ((jacocoReport in ItJacoco) dependsOn conditionalMerge).value
      )
}
