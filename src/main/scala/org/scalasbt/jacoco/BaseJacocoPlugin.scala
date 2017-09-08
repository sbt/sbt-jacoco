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

package org.scalasbt.jacoco

import java.io.File

import org.scalasbt.jacoco.build.BuildInfo
import sbt.Keys._
import sbt._
import sbt.plugins.JvmPlugin

private[jacoco] abstract class BaseJacocoPlugin
    extends AutoPlugin
    with Reporting
    with SavingData
    with Instrumentation
    with CommonKeys {

  protected def pluginConfig: Configuration

  override def requires: Plugins = JvmPlugin

  lazy val submoduleSettingsTask = Def.task {
    (
      (classesToCover in pluginConfig).?.value,
      (sourceDirectory in Compile).?.value,
      (jacocoDataFile in pluginConfig).?.value)
  }

  lazy val submoduleSettings =
    submoduleSettingsTask.all(ScopeFilter(inAggregates(ThisProject), inConfigurations(Compile, pluginConfig)))

  lazy val submoduleCoverTasks = (jacoco in pluginConfig).all(ScopeFilter(inAggregates(ThisProject)))

  def srcConfig: Configuration

  override def projectSettings: Seq[Setting[_]] =
    Seq(
      ivyConfigurations += pluginConfig,
      libraryDependencies += "org.jacoco" % "org.jacoco.agent" % BuildInfo.jacocoVersion % pluginConfig
    ) ++ inConfig(pluginConfig)(
      Defaults.testSettings ++
        JacocoDefaults.settings ++
        Seq(
          classesToCover := filterClassesToCover(
            (classDirectory in Compile).value,
            jacocoIncludes.value,
            jacocoExcludes.value),
          aggregateClassesToCover := submoduleSettings.value.flatMap(_._1).flatten.distinct,
          aggregateCoveredSources := submoduleSettings.value.flatMap(_._2).distinct,
          aggregateExecutionDataFiles := submoduleSettings.value.flatMap(_._3).distinct,
          fullClasspath := instrumentAction(
            (products in Compile).value,
            (fullClasspath in srcConfig).value,
            jacocoInstrumentedDirectory.value,
            update.value,
            fork.value,
            streams.value),
          javaOptions ++= {
            val dir = jacocoOutputDirectory.value
            if (fork.value) {
              Seq(s"-Djacoco-agent.destfile=${(dir / "jacoco.exec").absolutePath}")
            } else {
              Nil
            }
          },
          jacocoOutputDirectory in pluginConfig := crossTarget.value / pluginConfig.name,
          definedTests := (definedTests in srcConfig).value,
          definedTestNames := (definedTestNames in srcConfig).value,
          jacoco := (jacocoReport dependsOn jacocoCheck).value,
          jacocoAggregate := (jacocoAggregateReport dependsOn submoduleCoverTasks).value,
          jacocoCheck := Def
            .task(saveDataAction(jacocoDataFile.value, fork.value, streams.value))
            .dependsOn(test)
            .value,
          (jacocoDataFile in pluginConfig) := (jacocoOutputDirectory in pluginConfig).value / "jacoco.exec"
        ))

  private def filterClassesToCover(classes: File, incl: Seq[String], excl: Seq[String]) = {
    val inclFilters = incl map GlobFilter.apply
    val exclFilters = excl map GlobFilter.apply

    (PathFinder(classes) ** new FileFilter {
      def accept(f: File) = IO.relativize(classes, f) match {
        case Some(file) if !f.isDirectory && file.endsWith(".class") =>
          val name = toClassName(file)
          inclFilters.exists(_ accept name) && !exclFilters.exists(_ accept name)
        case _ => false
      }
    }).get
  }

  private def toClassName(entry: String): String =
    entry.stripSuffix(ClassExt).replace('/', '.')

  private val ClassExt = ".class"
}
