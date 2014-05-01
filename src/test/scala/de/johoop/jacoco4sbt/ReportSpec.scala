package de.johoop.jacoco4sbt

import org.mockito.Mockito._
import sbt.Keys._
import org.jacoco.core.analysis.{IBundleCoverage, ICounter}
import org.specs2.Specification
import sbt.Logger

class ReportSpec extends Specification {
  def is = s2"""
  $sequential
  ${"Report Unit Test".title}

  Report.checkCounter should
    return false if coverage is less than required           $e1
    return true if coverage is more than required            $e2
    return true if coverage is equals ro required            $e3
    return true if required coverage is 0 and ratio is NaN   $e4
    return true if required coverage is 0 and ratio is 0     $e5
  Report.checkCoverage should
    pass if all required coverage metrics are met            $e6
    fail if at least one metric is not met                   $e7
"""

  lazy val mockStreams = mock(classOf[TaskStreams])
  lazy val mockICounter = mock(classOf[ICounter])
  lazy val mockBundle = mock(classOf[IBundleCoverage])
  lazy val report = new Report(null, null, null, null, 0, null, null, null,
    Map("instruction" -> 35, "method" -> 40, "branch" -> 30, "complexity" -> 35, "line" -> 50, "class" -> 40),
    mockStreams)

  def before() = {
    val mockLog = mock(classOf[Logger])
    when(mockStreams.log).thenReturn(mockLog)
    when(mockBundle.getLineCounter).thenReturn(mockICounter)
    when(mockBundle.getInstructionCounter).thenReturn(mockICounter)
    when(mockBundle.getBranchCounter).thenReturn(mockICounter)
    when(mockBundle.getMethodCounter).thenReturn(mockICounter)
    when(mockBundle.getComplexityCounter).thenReturn(mockICounter)
    when(mockBundle.getClassCounter).thenReturn(mockICounter)

  }

  def e1 = {
    before()
    when(mockICounter.getMissedCount).thenReturn(30)
    when(mockICounter.getTotalCount).thenReturn(40)
    when(mockICounter.getCoveredRatio).thenReturn(0.25)
    report.checkCounter("foo", mockICounter, 50) mustEqual false
  }

  def e2 = {
    before()
    when(mockICounter.getMissedCount).thenReturn(10)
    when(mockICounter.getTotalCount).thenReturn(40)
    when(mockICounter.getCoveredRatio).thenReturn(0.75)
    report.checkCounter("foo", mockICounter, 50) mustEqual true
  }

  def e3 = {
    before()
    when(mockICounter.getMissedCount).thenReturn(20)
    when(mockICounter.getTotalCount).thenReturn(40)
    when(mockICounter.getCoveredRatio).thenReturn(0.50)
    report.checkCounter("foo", mockICounter, 50) mustEqual true
  }

  def e4 = {
    before()
    when(mockICounter.getMissedCount).thenReturn(0)
    when(mockICounter.getTotalCount).thenReturn(0)
    when(mockICounter.getCoveredRatio).thenReturn(Double.NaN)
    report.checkCounter("foo", mockICounter, 0) mustEqual true
  }

  def e5 = {
    before()
    when(mockICounter.getMissedCount).thenReturn(40)
    when(mockICounter.getTotalCount).thenReturn(0)
    when(mockICounter.getCoveredRatio).thenReturn(0)
    report.checkCounter("foo", mockICounter, 0) mustEqual true
  }

  def e6 = {
    when(mockICounter.getMissedCount).thenReturn(10)
    when(mockICounter.getTotalCount).thenReturn(40)
    when(mockICounter.getCoveredRatio).thenReturn(0.75)
    report.checkCoverage(mockBundle) mustEqual true

  }

  def e7 = {
    when(mockICounter.getMissedCount).thenReturn(10).thenReturn(30)
    when(mockICounter.getTotalCount).thenReturn(40)
    when(mockICounter.getCoveredRatio).thenReturn(0.75).thenReturn(0.25)
    report.checkCoverage(mockBundle) mustEqual false
  }
}
