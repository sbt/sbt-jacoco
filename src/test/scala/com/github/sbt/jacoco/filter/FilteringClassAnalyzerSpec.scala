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

import java.io.ByteArrayOutputStream

import org.jacoco.core.analysis.CoverageBuilder
import org.jacoco.core.data.ExecutionDataStore
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.collection.JavaConverters.*

class FilteringClassAnalyzerSpec extends AnyFlatSpec with Matchers {

  private val className = "com/github/sbt/jacoco/filter/FilterFixture"
  private val moduleName = "com/github/sbt/jacoco/filter/FilterFixture$"

  private def classBytes(internalName: String): Array[Byte] = {
    val stream = getClass.getClassLoader.getResourceAsStream(s"$internalName.class")
    try {
      val out = new ByteArrayOutputStream()
      val buffer = new Array[Byte](4096)
      var read = stream.read(buffer)
      while (read != -1) {
        out.write(buffer, 0, read)
        read = stream.read(buffer)
      }
      out.toByteArray
    } finally {
      stream.close()
    }
  }

  private def analyze(): CoverageBuilder = {
    val builder = new CoverageBuilder
    val analyzer = new FilteringAnalyzer(new ExecutionDataStore, builder)
    analyzer.analyzeClass(classBytes(className), className)
    analyzer.analyzeClass(classBytes(moduleName), moduleName)
    builder
  }

  private def methodNames(builder: CoverageBuilder, internalName: String): Set[String] = {
    val coverage = builder.getClasses.asScala.find(_.getName == internalName)
    coverage should not be empty
    coverage.get.getMethods.asScala.map(_.getName).toSet
  }

  "FilteringClassAnalyzer" should "keep the constructor and real methods of a case class" in {
    val names = methodNames(analyze(), className)
    names should contain("<init>")
    names should contain("realMethod")
  }

  it should "filter synthetic methods of a case class" in {
    val names = methodNames(analyze(), className)
    val filtered = Seq(
      "copy",
      "canEqual",
      "productPrefix",
      "productArity",
      "productElement",
      "productIterator",
      "productElementName",
      "productElementNames",
      "toString",
      "hashCode",
      "equals",
      "x",
      "apply",
      "unapply"
    )
    for (name <- filtered) {
      withClue(name) {
        names should not contain name
      }
    }
  }

  it should "keep the constructor of the companion module on Scala 2.12" in {
    // on Scala 3 the compiler marks the module constructor ACC_SYNTHETIC, so it is dropped during analysis
    assume(scala.util.Properties.versionNumberString.startsWith("2.12"))
    val names = methodNames(analyze(), moduleName)
    names should contain("<init>")
  }

  it should "filter synthetic methods of the companion module" in {
    val names = methodNames(analyze(), moduleName)
    for (name <- Seq("apply", "unapply", "readResolve", "writeReplace", "toString", "<clinit>")) {
      withClue(name) {
        names should not contain name
      }
    }
  }

  it should "produce coverage nodes that bundle without errors" in {
    noException should be thrownBy analyze().getBundle("test")
  }
}
