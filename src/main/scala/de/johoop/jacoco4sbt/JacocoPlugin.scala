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

import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

object JacocoPlugin extends AutoPlugin {
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
}
