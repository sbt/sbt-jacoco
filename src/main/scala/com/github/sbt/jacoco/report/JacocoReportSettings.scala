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

package com.github.sbt.jacoco.report

import com.github.sbt.jacoco.report.formats.JacocoReportFormat

case class JacocoReportSettings(
    title: String = "Jacoco Coverage Report",
    subDirectory: Option[String] = None,
    thresholds: JacocoThresholds = JacocoThresholds(),
    formats: Seq[JacocoReportFormat] = Seq(JacocoReportFormats.ScalaHTML),
    fileEncoding: String = "utf-8") {

  def withTitle(title: String): JacocoReportSettings = {
    copy(title = title)
  }

  def withSubDirectory(subDirectory: String): JacocoReportSettings = {
    copy(subDirectory = Some(subDirectory))
  }

  def withThresholds(thresholds: JacocoThresholds): JacocoReportSettings = {
    copy(thresholds = thresholds)
  }

  def withFormats(formats: JacocoReportFormat*): JacocoReportSettings = {
    copy(formats = formats)
  }

  def withFileEncoding(fileEncoding: String): JacocoReportSettings = {
    copy(fileEncoding = fileEncoding)
  }
}
