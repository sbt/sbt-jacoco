package de.johoop.jacoco4sbt

import java.io.File

import de.johoop.jacoco4sbt.build.BuildInfo
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

private[jacoco4sbt] abstract class BaseJacocoPlugin
    extends AutoPlugin
    with Reporting
    with SavingData
    with Instrumentation
    with CommonKeys {

  protected def pluginConfig: Configuration

  override def requires: Plugins = JvmPlugin

  lazy val submoduleSettingsTask = Def.task {
    (
      (classesToCover in pluginConfig ?).value,
      (sourceDirectory in Compile ?).value,
      (executionDataFile in pluginConfig ?).value)
  }

  lazy val submoduleSettings =
    submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject), inConfigurations(Compile, pluginConfig)))

  lazy val submoduleCoverTasks = (cover in pluginConfig).all(ScopeFilter(inAggregates(ThisProject)))

  def srcConfig: Configuration

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      ivyConfigurations += pluginConfig,
      libraryDependencies +=
        "org.jacoco" % "org.jacoco.agent" % BuildInfo.jacocoVersion % "jacoco" artifacts Artifact(
          "org.jacoco.agent",
          "jar",
          "jar")
    ) ++ inConfig(pluginConfig)(
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
        outputDirectory in pluginConfig := crossTarget.value / pluginConfig.name,
        definedTests := (definedTests in srcConfig).value,
        definedTestNames := (definedTestNames in srcConfig).value,
        cover := (report dependsOn check).value,
        aggregateCover := (aggregateReport dependsOn submoduleCoverTasks).value,
        check := Def.task(saveDataAction(executionDataFile.value, fork.value, streams.value)).dependsOn(test).value
      ))

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
}
