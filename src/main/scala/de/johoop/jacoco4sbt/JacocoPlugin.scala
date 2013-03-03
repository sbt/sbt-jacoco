/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011-2013 Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.johoop.jacoco4sbt

import sbt._
import Keys._
import java.io.File
import inc.Locate

object JacocoPlugin extends Plugin {

  private object JacocoDefaults extends Reporting with Keys {
    val settings = Seq(
      outputDirectory <<= (crossTarget) { _ / "jacoco" },
      reportFormats := Seq(HTMLReport()),
      reportTitle := "Jacoco Coverage Report",
      sourceTabWidth := 2,
      sourceEncoding := "utf-8",

      includes := Seq("*"),

      excludes := Seq(),

      coveredSources <<= (sourceDirectories in Compile) map identity,

      instrumentedClassDirectory <<= (outputDirectory, classDirectory in Compile) (_ / _.getName),

      report <<= (outputDirectory, reportFormats, reportTitle, coveredSources, classesToCover,
        sourceEncoding, sourceTabWidth, streams) map reportAction,

      clean <<= outputDirectory map (dir => if (dir.exists) IO delete dir.listFiles)
    )
  }

  private def filterClassesToCover(classes: File, incl: Seq[String], excl: Seq[String]) = {
    val inclFilters = incl map GlobFilter.apply
    val exclFilters = excl map GlobFilter.apply

    PathFinder(classes) ** new FileFilter {
      def accept(f: File) = IO.relativize(classes, f) match {
        case Some(file) if ! f.isDirectory && file.endsWith(".class") =>
          val name = Locate.toClassName(file)
          inclFilters.exists(_ accept name) && ! exclFilters.exists(_ accept name)
        case _ => false
      }
    } get
  }

  object jacoco extends Reporting with SavingData with Instrumentation with Keys {

    val settings = Seq(ivyConfigurations += Config) ++
      inConfig(Config)(Defaults.testTasks ++ JacocoDefaults.settings ++ Seq(

      jacoco.classesToCover in jacoco.Config <<= (classDirectory in Compile, includes, excludes) map (filterClassesToCover),

      definedTests <<= definedTests in Test,
      definedTestNames <<= definedTestNames in Test,

      fullClasspath <<= (products in Compile, fullClasspath in Test, instrumentedClassDirectory, streams) map instrumentAction,
      cover <<= report dependsOn check,
      check <<= ((outputDirectory, streams) map saveDataAction) dependsOn test
      ))
  }

  object itJacoco extends Reporting with Merging with SavingData with Instrumentation with IntegrationTestKeys {

    val conditionalMerge = (outputDirectory, outputDirectory in jacoco.Config, streams, mergeReports) map conditionalMergeAction
    val forceMerge = (outputDirectory, outputDirectory in jacoco.Config, streams) map mergeAction

    val settings = Seq(ivyConfigurations += Config) ++ inConfig(Config)(Defaults.testTasks ++ JacocoDefaults.settings ++ Seq(

      outputDirectory <<= (crossTarget) { _ / "it-jacoco" },

      itJacoco.classesToCover in itJacoco.Config <<= (classDirectory in Compile, includes, excludes) map (filterClassesToCover),

      definedTests <<= definedTests in IntegrationTest,
      definedTestNames <<= definedTestNames in IntegrationTest,

      fullClasspath <<= (products in Compile, fullClasspath in IntegrationTest, instrumentedClassDirectory, streams) map instrumentAction,
      report <<= report dependsOn conditionalMerge,
      cover <<= report dependsOn check,

      check <<= ((outputDirectory, streams) map saveDataAction) dependsOn test,
      merge <<= forceMerge,
      mergeReports := true
    ))
  }
}
