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

import org.jacoco.report.IReportVisitor

trait JacocoReportFormat {
  def createVisitor(destDirectory: File, encoding: String): IReportVisitor
}
