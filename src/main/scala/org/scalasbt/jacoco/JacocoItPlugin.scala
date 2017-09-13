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

object JacocoItPlugin extends BaseJacocoPlugin with Merging {

  object autoImport {
    lazy val jacocoMerge: TaskKey[Unit] = taskKey[Unit]("Merges all '*.exec' files into a single data file.")

    lazy val mergedExecutionDataFile: SettingKey[File] =
      settingKey[File]("Execution data file contain unit test and integration test data.")

    lazy val mergeReports: SettingKey[Boolean] =
      settingKey[Boolean]("Indication whether to merge the unittest and integration test reports. Defaults to true.")
  }

  import autoImport._

  lazy val srcConfig: Configuration = IntegrationTest

  lazy val conditionalMerge: Def.Initialize[Task[Unit]] = Def.task {
    conditionalMergeAction(
      (jacocoDataFile in Test).value,
      (jacocoDataFile in IntegrationTest).value,
      (mergedExecutionDataFile in IntegrationTest).value,
      streams.value,
      mergeReports.value)
  }

  lazy val forceMerge: Def.Initialize[Task[Unit]] = Def.task {
    mergeAction(
      (jacocoDataFile in Test).value,
      (jacocoDataFile in IntegrationTest).value,
      (mergedExecutionDataFile in IntegrationTest).value,
      streams.value)
  }

  override def projectSettings: Seq[Setting[_]] =
    Defaults.itSettings ++
    super.projectSettings ++
      Seq(
        (jacocoDataFile in IntegrationTest) := crossTarget.value / "jacoco-it.exec",
        jacocoMerge := forceMerge.value,
        mergeReports := true,
        (mergedExecutionDataFile in IntegrationTest) := crossTarget.value / "jacoco-merged.exec",
        (jacocoReport in IntegrationTest) := Reporting.reportAction(
          (jacocoOutputDirectory in IntegrationTest).value,
          (mergedExecutionDataFile in IntegrationTest).value,
          (jacocoReportSettings in IntegrationTest).value,
          (coveredSources in IntegrationTest).value,
          (classesToCover in IntegrationTest).value,
          (jacocoSourceSettings in IntegrationTest).value,
          (streams in IntegrationTest).value
        ),
        jacocoReport in IntegrationTest := ((jacocoReport in IntegrationTest) dependsOn conditionalMerge).value,
        jacoco in IntegrationTest := (jacoco in IntegrationTest).value
      )
}
