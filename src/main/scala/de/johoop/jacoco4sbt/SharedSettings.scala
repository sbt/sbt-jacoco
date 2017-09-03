package de.johoop.jacoco4sbt

import java.io.File

import de.johoop.jacoco4sbt.build.BuildInfo
import sbt.Keys._
import sbt._

private[jacoco4sbt] trait SharedSettings { _: Reporting with SavingData with Instrumentation with Keys =>

  lazy val submoduleSettingsTask = Def.task {
    ((classesToCover in Config ?).value, (sourceDirectory in Compile ?).value, (executionDataFile in Config ?).value)
  }

  val submoduleSettings =
    submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject), inConfigurations(Compile, Config)))

  val submoduleCoverTasks = (cover in Config).all(ScopeFilter(inAggregates(ThisProject)))

  def srcConfig: Configuration

  def settings =
    Seq(
      ivyConfigurations += Config,
      libraryDependencies +=
        "org.jacoco" % "org.jacoco.agent" % BuildInfo.jacocoVersion % "jacoco" artifacts Artifact(
          "org.jacoco.agent",
          "jar",
          "jar")
    ) ++ inConfig(Config)(
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
        outputDirectory in Config := crossTarget.value / Config.name,
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
