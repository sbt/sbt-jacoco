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

package com.github.sbt.jacoco

import java.io.File

import org.jacoco.core.analysis.{IBundleCoverage, ICounter}
import org.mockito.Mockito.{mock, when}
import com.github.sbt.jacoco.report.{JacocoReportSettings, JacocoSourceSettings, JacocoThresholds, Report}
import sbt.Keys.TaskStreams
import sbt.Logger

class TestCounters {
  private val mockStreams = mock(classOf[TaskStreams])
  private val mockICounter = mock(classOf[ICounter])
  private val mockBundle = mock(classOf[IBundleCoverage])
  private val mockLog = mock(classOf[Logger])

  private val report =
    new Report(
      streams = mockStreams,
      sourceSettings = JacocoSourceSettings(),
      reportSettings = JacocoReportSettings(
        thresholds =
          JacocoThresholds(instruction = 35, method = 40, branch = 30, complexity = 35, line = 50, clazz = 40)),
      reportDirectory = new File("."),
      executionDataFiles = Nil,
      classDirectories = Nil,
      sourceDirectories = Nil,
      checkCoverage = true
    )

  when[Logger](mockStreams.log).thenReturn(mockLog)
  when(mockBundle.getLineCounter).thenReturn(mockICounter)
  when(mockBundle.getInstructionCounter).thenReturn(mockICounter)
  when(mockBundle.getBranchCounter).thenReturn(mockICounter)
  when(mockBundle.getMethodCounter).thenReturn(mockICounter)
  when(mockBundle.getComplexityCounter).thenReturn(mockICounter)
  when(mockBundle.getClassCounter).thenReturn(mockICounter)

  def stubCounters(missedCount: Int, totalCount: Int, coveredRatio: Double): Unit = {
    when(mockICounter.getMissedCount).thenReturn(missedCount)
    when(mockICounter.getTotalCount).thenReturn(totalCount)
    when(mockICounter.getCoveredRatio).thenReturn(coveredRatio)
  }

  def stubCounters(missedCount: Seq[Int], totalCount: Seq[Int], coveredRatio: Seq[Double]): Unit = {
    missedCount.foldLeft(when(mockICounter.getMissedCount)) { (acc, c) =>
      acc.thenReturn(c)
    }
    totalCount.foldLeft(when(mockICounter.getTotalCount)) { (acc, c) =>
      acc.thenReturn(c)
    }
    coveredRatio.foldLeft(when(mockICounter.getCoveredRatio)) { (acc, c) =>
      acc.thenReturn(c)
    }
  }

  def checkCounter(required: Double): Boolean = {
    report.checkCounter("foo", mockICounter, required, StringBuilder.newBuilder)
  }

  def checkBundle(): Boolean = {
    report.checkCoverage(mockBundle)
  }
}
