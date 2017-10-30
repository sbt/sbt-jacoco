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

package com.github.sbt.jacoco.coveralls

import java.io.File

import com.github.sbt.jacoco.{JacocoPlugin, _}
import com.github.sbt.jacoco.report.ReportUtils
import sbt.Keys._
import sbt._

object JacocoCoverallsPlugin extends BaseJacocoPlugin {
  override def requires: Plugins = JacocoPlugin
  override def trigger: PluginTrigger = noTrigger

  override protected def srcConfig = Test

  object autoImport {
    val jacocoCoveralls: TaskKey[Unit] = taskKey("Generate and upload JaCoCo reports to Coveralls")
    val jacocoCoverallsGenerateReport: TaskKey[Unit] = taskKey("Generate Coveralls report JSON")

    val jacocoCoverallsServiceName: SettingKey[String] = settingKey("CI service name")
    val jacocoCoverallsBuildNumber: SettingKey[Option[String]] = settingKey("Build number to send to Coveralls")
    val jacocoCoverallsJobId: SettingKey[Option[String]] = settingKey("Build job ID to send to Coveralls")
    val jacocoCoverallsPullRequest: SettingKey[Option[String]] = settingKey("Pull request number to send to Coveralls")
    val jacocoCoverallsOutput: SettingKey[File] = settingKey("File to store Coveralls coverage")
    val jacocoCoverallsRepoToken: SettingKey[Option[String]] = settingKey("Coveralls repo secret key")
  }

  import autoImport._ // scalastyle:ignore import.grouping

  override def projectSettings: Seq[Setting[_]] = Seq(
    jacocoCoverallsOutput := jacocoReportDirectory.value,
    jacocoCoveralls := Def.task {
      CoverallsClient.sendReport(jacocoCoverallsOutput.value / "coveralls.json", streams.value)
    }.value,
    jacocoCoverallsGenerateReport := Def.task {
      val coverallsFormat =
        new CoverallsReportFormat(
          coveredSources.value,
          baseDirectory.value,
          jacocoCoverallsServiceName.value,
          jacocoCoverallsJobId.value,
          jacocoCoverallsBuildNumber.value,
          jacocoCoverallsPullRequest.value,
          jacocoCoverallsRepoToken.value
        )

      ReportUtils.generateReport(
        jacocoCoverallsOutput.value,
        jacocoDataFile.value,
        jacocoReportSettings.value.withFormats(coverallsFormat),
        coveredSources.value,
        classesToCover.value,
        jacocoSourceSettings.value,
        streams.value,
        checkCoverage = false
      )
    }.value,
    jacocoCoveralls := (jacocoCoveralls dependsOn jacocoCoverallsGenerateReport).value,
    // TODO fail if no job id
    // TODO manual job id
    jacocoCoverallsServiceName := "travis-ci",
    jacocoCoverallsJobId := sys.env.get("TRAVIS_JOB_ID"),
    jacocoCoverallsBuildNumber := sys.env.get("TRAVIS_JOB_NUMBER"),
    jacocoCoverallsPullRequest := {
      sys.env.get("TRAVIS_PULL_REQUEST") match {
        case Some("false") => None
        case Some(pr) => Some(pr)
        case _ => None
      }
    },
    jacocoCoverallsRepoToken := None
  )
}
