package de.johoop.jacoco4sbt

import de.johoop.jacoco4sbt.JacocoPlugin.autoImport.Jacoco
import sbt.Keys._
import sbt._

object JacocoItPlugin extends BaseJacocoPlugin with Merging {

  object autoImport {
    lazy val ItJacoco = config("it-jacoco").extend(IntegrationTest).hide

    lazy val merge = TaskKey[Unit]("merge", "Merges all '*.exec' files into a single data file.")
    lazy val mergeReports = SettingKey[Boolean](
      "merge-reports",
      "Indication whether to merge the unittest and integration test reports. Defaults to true.")
  }

  import autoImport._

  override protected val pluginConfig: Configuration = ItJacoco
  lazy val srcConfig = IntegrationTest

  lazy val conditionalMerge = Def.task {
    conditionalMergeAction(
      (outputDirectory in ItJacoco).value,
      (outputDirectory in Jacoco).value,
      streams.value,
      mergeReports.value)
  }

  lazy val forceMerge = Def.task {
    mergeAction((outputDirectory in ItJacoco).value, (outputDirectory in Jacoco).value, streams.value)
  }

  override def projectSettings: Seq[Setting[_]] =
    super.projectSettings ++
      Seq(
        report in ItJacoco := ((report in ItJacoco) dependsOn conditionalMerge).value,
        merge := forceMerge.value,
        mergeReports := true,
        (executionDataFile in ItJacoco) := (outputDirectory in ItJacoco).value / "jacoco-merged.exec"
      )
}
