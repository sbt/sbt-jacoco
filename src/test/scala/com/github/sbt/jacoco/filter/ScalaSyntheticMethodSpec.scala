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

package com.github.sbt.jacoco.filter

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class ScalaSyntheticMethodSpec extends AnyFlatSpec with Matchers {

  private val instanceOwner = "com/example/Foo"
  private val moduleOwner = "com/example/Foo$"

  private val instanceMethodNames = Seq(
    "canEqual",
    "copy",
    "equals",
    "hashCode",
    "productPrefix",
    "productArity",
    "productElement",
    "productElementName",
    "productElementNames",
    "productIterator",
    "toString"
  )

  private val companionMethodNames = Seq(
    "apply",
    "unapply",
    "unapplySeq",
    "readResolve",
    "writeReplace",
    "equals$extension",
    "hashCode$extension"
  )

  "isSyntheticMethod" should "filter one-line case-class instance methods on instance classes" in {
    for (name <- instanceMethodNames) {
      withClue(name) {
        ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, name, 3, 3) shouldBe true
      }
    }
  }

  it should "filter one-line case-class instance methods on module classes" in {
    for (name <- instanceMethodNames) {
      withClue(name) {
        ScalaSyntheticMethod.isSyntheticMethod(moduleOwner, name, 3, 3) shouldBe true
      }
    }
  }

  it should "filter one-line companion methods on module classes only" in {
    for (name <- companionMethodNames) {
      withClue(name) {
        ScalaSyntheticMethod.isSyntheticMethod(moduleOwner, name, 3, 3) shouldBe true
        ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, name, 3, 3) shouldBe false
      }
    }
  }

  it should "filter copy$default accessors" in {
    ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, "copy$default$1", 3, 3) shouldBe true
    ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, "copy$default$12", 3, 3) shouldBe true
  }

  it should "not filter methods spanning more than one line" in {
    for (name <- instanceMethodNames) {
      withClue(name) {
        ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, name, 3, 5) shouldBe false
      }
    }
  }

  it should "not filter methods with other names" in {
    ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, "realMethod", 3, 3) shouldBe false
    ScalaSyntheticMethod.isSyntheticMethod(instanceOwner, "productElementNamesX", 3, 3) shouldBe false
    ScalaSyntheticMethod.isSyntheticMethod(moduleOwner, "realMethod", 3, 3) shouldBe false
  }
}
