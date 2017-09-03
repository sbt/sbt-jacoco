package de.johoop.jacoco4sbt

import de.johoop.jacoco4sbt.JacocoPlugin.autoImport.Jacoco
import sbt.Keys._
import sbt._

object JacocoItPlugin extends BaseJacocoPlugin with Merging {

  object autoImport {
    lazy val ItJacoco = config("it-jacoco").extend(IntegrationTest).hide

    lazy val merge = TaskKey[Unit]("merge", "Merges all '*.exec' files into a single data file.")

    lazy val mergedExecutionDataFile =
      SettingKey[File]("merged-execution-data-file", "Execution data file contain unit test and integration test data.")

    lazy val mergeReports = SettingKey[Boolean](
      "merge-reports",
      "Indication whether to merge the unittest and integration test reports. Defaults to true.")
  }

  import autoImport._

  override protected val pluginConfig: Configuration = ItJacoco
  lazy val srcConfig = IntegrationTest

  lazy val conditionalMerge = Def.task {
    conditionalMergeAction(
      (executionDataFile in Jacoco).value,
      (executionDataFile in ItJacoco).value,
      (mergedExecutionDataFile in ItJacoco).value,
      streams.value,
      mergeReports.value)
  }

  lazy val forceMerge = Def.task {
    mergeAction(
      (executionDataFile in Jacoco).value,
      (executionDataFile in ItJacoco).value,
      (mergedExecutionDataFile in ItJacoco).value,
      streams.value)
  }

  override def projectSettings: Seq[Setting[_]] =
    super.projectSettings ++
      Seq(
        report in ItJacoco := ((report in ItJacoco) dependsOn conditionalMerge).value,
        merge := forceMerge.value,
        mergeReports := true,
        (mergedExecutionDataFile in ItJacoco):= (outputDirectory in ItJacoco).value / "jacoco-merged.exec"
      )
}
