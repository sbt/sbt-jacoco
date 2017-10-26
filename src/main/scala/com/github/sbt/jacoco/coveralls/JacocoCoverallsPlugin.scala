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
    val jacocoCoveralls: TaskKey[Unit] = taskKey("Upload JaCoCo reports to Coveralls")

    val jacocoCoverallsJobId: SettingKey[String] = settingKey("todo")
    val jacocoCoverallsGenerateReport: TaskKey[Unit] = taskKey("TODO")
    val jacocoCoverallsOutput: SettingKey[File] = settingKey("File to store Coveralls coverage")

    val jacocoCoverallsRepoToken: SettingKey[Option[String]] = settingKey("todo")
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
          jacocoCoverallsJobId.value,
          jacocoCoverallsRepoToken.value)

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
    jacocoCoverallsJobId := sys.env.getOrElse("TRAVIS_JOB_ID", "unknown"),
    jacocoCoverallsRepoToken := None
  )
}
