/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011 Joachim Hofer
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

trait Keys {
  lazy val Config = config("jacoco") hide

  lazy val targetDirectory = SettingKey[File]("jacoco-target-directory", "Where JaCoCo should store its execution data and reports.")
  lazy val reportFormat = SettingKey[FormattedReport]("jacoco-report-format", "Format (XML, CSV, HTML) of JaCoCo report to generate.")
  lazy val sourceTabWidth = SettingKey[Int]("jacoco-source-tab-width", "Tab width of the sources to display in the JaCoCo report.")
  lazy val sourceEncoding = SettingKey[String]("jacoco-source-encoding", "Encoding of the source files (for JaCoCo reporting).")
  
  lazy val jacocoClasspath = TaskKey[Classpath]("jacoco-classpath", "Internal JaCoCo classpath.")
  lazy val jacocoReport = TaskKey[Unit]("jacoco-report", "Generates a JaCoCo report. You can use the 'jacoco report' command alternatively.")

  lazy val unpackJacocoAgent = TaskKey[File]("unpack-jacoco-agent", "Unpacks the Jacoco Agent JAR")
}
