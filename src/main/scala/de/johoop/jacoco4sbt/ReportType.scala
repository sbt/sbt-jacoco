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

import org.jacoco.report.FileMultiReportOutput
import org.jacoco.report.IMultiReportOutput
import org.jacoco.report.IReportVisitor
import org.jacoco.report.xml.XMLFormatter
import org.jacoco.report.html.HTMLFormatter
import org.jacoco.report.csv.CSVFormatter
import java.io.File
import java.io.FileOutputStream

object ReportType extends Enumeration {
  
  type ReportType = Value
  
  val XML, HTML, CSV = Value
  
  private[jacoco4sbt] def reportVisitor(reportType: ReportType, encoding: String, dir: File) = reportType match {
    case HTML => {
      val formatter = new HTMLFormatter
      formatter setOutputEncoding encoding
      formatter createVisitor new FileMultiReportOutput(new File(dir, "html"))
    }
    
    case XML => {
      val formatter = new XMLFormatter
      formatter setOutputEncoding encoding
      formatter createVisitor new FileOutputStream(new File(dir, "jacoco.xml"))
    }
    
    case CSV => {
      val formatter = new CSVFormatter
      formatter setOutputEncoding encoding
      formatter createVisitor new FileOutputStream(new File(dir, "jacoco.csv"))
    }
  }
}
