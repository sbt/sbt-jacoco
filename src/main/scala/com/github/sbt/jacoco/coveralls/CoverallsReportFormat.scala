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

import com.github.sbt.jacoco.report.formats.JacocoReportFormat
import org.jacoco.report.IReportVisitor
import sbt._

class CoverallsReportFormat(
    sourceDirs: Seq[File],
    projectRootDir: File,
    serviceName: String,
    jobId: String,
    buildNumber: Option[String],
    pullRequest: Option[String],
    repoToken: Option[String])
    extends JacocoReportFormat {

  override def createVisitor(directory: File, encoding: String): IReportVisitor = {
    IO.createDirectory(directory)

    new CoverallsReportVisitor(
      directory / "coveralls.json",
      sourceDirs,
      projectRootDir,
      serviceName,
      jobId,
      buildNumber,
      pullRequest,
      repoToken)
  }
}
