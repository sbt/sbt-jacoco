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

import org.scalatest.{FlatSpec, Matchers}

class ReportSpec extends FlatSpec with Matchers {
  val testCounters = new TestCounters()

  "Report.checkCounter" should "return false if coverage is less than required" in {
    testCounters.stubCounters(30, 40, 0.25)
    testCounters.checkCounter(50) shouldBe false
  }

  it should "return true if coverage is more than required" in {
    testCounters.stubCounters(10, 40, 0.75)
    testCounters.checkCounter(50) shouldBe true
  }

  it should "if coverage is equals to required" in {
    testCounters.stubCounters(20, 40, 0.50)
    testCounters.checkCounter(50) shouldBe true
  }

  it should "return true if required coverage is 0 and ratio is NaN" in {
    testCounters.stubCounters(0, 0, Double.NaN)
    testCounters.checkCounter(0) shouldBe true
  }

  it should "return true if required coverage is 0 and ratio is 0" in {
    testCounters.stubCounters(40, 0, 0)
    testCounters.checkCounter(0) shouldBe true
  }

  "Report.checkCoverage" should "return true if all required coverage metrics are met" in {
    testCounters.stubCounters(10, 40, 0.75)
    testCounters.checkBundle() shouldBe true
  }

  it should "return false if at least one metric is not met" in {
    testCounters.stubCounters(Seq(10, 30), Seq(40), Seq(0.75, 0.25))
    testCounters.checkBundle() shouldBe false
  }
}
