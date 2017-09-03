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

package de.johoop.jacoco4sbt

import org.specs2.Specification

class ReportSpec extends Specification {
  def is = args(sequential = true) ^ s2"""
  Report Unit Test

  Report.checkCounter should
    return false if coverage is less than required           $e1
    return true if coverage is more than required            $e2
    return true if coverage is equals ro required            $e3
    return true if required coverage is 0 and ratio is NaN   $e4
    return true if required coverage is 0 and ratio is 0     $e5
  Report.checkCoverage should
    return true if all required coverage metrics are met     $e6
    return false if at least one metric is not met           $e7
"""

  val testCounters = new TestCounters()

  def e1 = {
    testCounters.stubCounters(30, 40, 0.25)
    testCounters.checkCounter(50) mustEqual false
  }

  def e2 = {
    testCounters.stubCounters(10, 40, 0.75)
    testCounters.checkCounter(50) mustEqual true
  }

  def e3 = {
    testCounters.stubCounters(20, 40, 0.50)
    testCounters.checkCounter(50) mustEqual true
  }

  def e4 = {
    testCounters.stubCounters(0, 0, Double.NaN)
    testCounters.checkCounter(0) mustEqual true
  }

  def e5 = {
    testCounters.stubCounters(40, 0, 0)
    testCounters.checkCounter(0) mustEqual true
  }

  def e6 = {
    testCounters.stubCounters(10, 40, 0.75)
    testCounters.checkBundle() mustEqual true
  }

  def e7 = {
    testCounters.stubCounters(Seq(10, 30), Seq(40), Seq(0.75, 0.25))
    testCounters.checkBundle() mustEqual false
  }
}
