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

package de.johoop.jacoco4sbt.report.formats

import java.io.File

import de.johoop.jacoco4sbt.ScalaHtmlFormatter
import org.jacoco.report.{FileMultiReportOutput, IReportVisitor}

class ScalaHTMLReportFormat(withBranchCoverage: Boolean = true) extends JacocoReportFormat {
  override def createVisitor(directory: File, encoding: String): IReportVisitor = {
    val formatter = new ScalaHtmlFormatter(withBranchCoverage)
    formatter.setOutputEncoding(encoding)
    formatter.createVisitor(new FileMultiReportOutput(new File(directory, "html")))
  }

  def withoutBranches: ScalaHTMLReportFormat = new ScalaHTMLReportFormat(withBranchCoverage = false)
}
