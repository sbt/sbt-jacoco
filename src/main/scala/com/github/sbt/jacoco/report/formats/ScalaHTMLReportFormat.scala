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

package com.github.sbt.jacoco.report.formats

import java.io.File

import org.jacoco.core.analysis.ICoverageNode
import org.jacoco.report.html.HTMLFormatter
import org.jacoco.report.internal.html.resources.Styles
import org.jacoco.report.internal.html.table._
import org.jacoco.report.{FileMultiReportOutput, IReportVisitor}
import sbt._

class ScalaHTMLReportFormat(withBranchCoverage: Boolean = true) extends JacocoReportFormat {
  override def createVisitor(directory: File, encoding: String): IReportVisitor = {
    IO.createDirectory(directory)

    val formatter = new ScalaHtmlFormatter(withBranchCoverage)
    formatter.setOutputEncoding(encoding)
    formatter.createVisitor(new FileMultiReportOutput(new File(directory, "html")))
  }

  def withoutBranches: ScalaHTMLReportFormat = new ScalaHTMLReportFormat(withBranchCoverage = false)
}

/**
  * Omits displaying instruction and branch coverage in the coverage tables,
  * as Scala generates null checks which make these too noisy.
  *
  * TODO: Find a way to remove them from the annotated source code reports, too.
  */
class ScalaHtmlFormatter(withBranchCoverage: Boolean) extends HTMLFormatter {
  private val table: Table = createTable

  setLanguageNames(new ScalaLanguageNames)

  override def getTable: Table = table

  private def createTable: Table = {
    val t: Table = new Table
    t.add("Element", null, new LabelColumn, false) // scalastyle:ignore null
    t.add("Missed Lines", Styles.BAR, new BarColumn(ICoverageNode.CounterEntity.LINE, getLocale), true)
    t.add("Total Lines", Styles.CTR1, CounterColumn.newTotal(ICoverageNode.CounterEntity.LINE, getLocale), false)
    t.add("Cov.", Styles.CTR2, new PercentageColumn(ICoverageNode.CounterEntity.LINE, getLocale), false)
    if (withBranchCoverage) {
      t.add("Missed Branches", Styles.BAR, new BarColumn(ICoverageNode.CounterEntity.BRANCH, getLocale), false)
      t.add("Cov.", Styles.CTR2, new PercentageColumn(ICoverageNode.CounterEntity.BRANCH, getLocale), false)
    }
    addMissedTotalColumns(t, "Methods", ICoverageNode.CounterEntity.METHOD)
    addMissedTotalColumns(t, "Classes", ICoverageNode.CounterEntity.CLASS)

    t
  }

  private def addMissedTotalColumns(table: Table, label: String, entity: ICoverageNode.CounterEntity): Unit = {
    table.add("Missed", Styles.CTR1, CounterColumn.newMissed(entity, getLocale), false)
    table.add(label, Styles.CTR2, CounterColumn.newTotal(entity, getLocale), false)
  }
}
