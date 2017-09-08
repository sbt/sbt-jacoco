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

package org.scalasbt.jacoco.report.formats

import java.io.{File, FileOutputStream}

import org.jacoco.report.IReportVisitor
import org.jacoco.report.xml.XMLFormatter

class XMLReportFormat extends JacocoReportFormat {
  override def createVisitor(directory: File, encoding: String): IReportVisitor = {
    val formatter = new XMLFormatter()
    formatter.setOutputEncoding(encoding)
    formatter.createVisitor(new FileOutputStream(new File(directory, "jacoco.xml")))
  }
}
