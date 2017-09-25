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

import com.github.sbt.jacoco.report.formats.{CSVReportFormat, HTMLReportFormat, ScalaHTMLReportFormat, XMLReportFormat}

object JacocoReportFormats {

  /** HTML report containing detailed metrics at instruction level and embedded source code. */
  val ScalaHTML = new ScalaHTMLReportFormat()

  /** HTML report containing detailed metrics at instruction level and embedded source code.
    *
    * '''Note:''' does not support Scala language constructs.
    */
  val HTML = new HTMLReportFormat()

  /** XML report containing detailed metrics at instruction level.
    *
    * '''Note:''' does not support Scala language constructs.
    */
  val XML = new XMLReportFormat()

  /** CSV report containing metrics at class level only.
    *
    * '''Note:''' does not support Scala language constructs.
    */
  val CSV = new CSVReportFormat()
}
