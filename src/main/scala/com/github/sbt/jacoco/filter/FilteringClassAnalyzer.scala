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

import com.github.sbt.jacoco.filter.AccessorDetector._
import com.github.sbt.jacoco.filter.ScalaForwarderDetector._
import com.github.sbt.jacoco.filter.ScalaSyntheticMethod._
import org.jacoco.core.analysis.{Analyzer, ICoverageVisitor, IMethodCoverage}
import org.jacoco.core.data.ExecutionDataStore
import org.jacoco.core.internal.analysis.{ClassAnalyzer, ClassCoverageImpl, MethodAnalyzer, StringPool}
import org.jacoco.core.internal.data.CRC64
import org.jacoco.core.internal.flow.{ClassProbesAdapter, MethodProbesVisitor}
import org.jacoco.core.internal.instr.InstrSupport
import org.objectweb.asm._
import org.objectweb.asm.tree.{ClassNode, MethodNode}

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Filters coverage results from Scala synthetic methods:
  * - trait forwarders
  * - case class toString / equals / apply / unapply
  *
  * These are identified by the heuristic that they have the same line number as a constructor, or
  * the same line as other one-line methods if we are in a module class.
  *
  * This filtering should really happen in Jacoco core, but the API for this is not available and
  * scheduled for Q1 2014.
  *
  * See [[https://github.com/jacoco/jacoco/wiki/FilteringOptions]] and [[https://github.com/jacoco/jacoco/issues/139]]
  * for more discussion of the JaCoCo roadmap.
  *
  * These filters are based on [[https://github.com/timezra/jacoco/commit/b6146ebed8b8e7507ec634ee565fe03f3e940fdd]],
  * but extended to correctly exclude synthetics in module classes.
  */
private final class FilteringClassAnalyzer(
    classCoverage: ClassCoverageImpl,
    classNode: ClassNode,
    probes: Array[Boolean],
    stringPool: StringPool,
    coverageVisitor: ICoverageVisitor)
    extends ClassAnalyzer(classCoverage, probes, stringPool) {

  private val coverages = mutable.Buffer[IMethodCoverage]()

  override def visitMethod(
      access: Int,
      name: String,
      desc: String,
      signature: String,
      exceptions: Array[String]): MethodProbesVisitor = {
    InstrSupport.assertNotInstrumented(name, classCoverage.getName)
    if ((access & Opcodes.ACC_SYNTHETIC) != 0) {
      null // scalastyle:ignore null
    } else {
      super.visitMethod(access, name, desc, signature, exceptions)
    }
  }

  override def visitEnd(): Unit = {
    try visitFiltered()
    finally {
      super.visitEnd()
      coverageVisitor.visitCoverage(classCoverage)
    }
  }

  private val isModuleClass = classCoverage.getName.endsWith("$")

  private val methods: Seq[MethodNode] = classNode.methods.asScala

  private def visitFiltered(): Unit = {
    for {
      mc <- coverages
      methodNode = methods.find(m => m.name == mc.getName && m.desc == mc.getDesc).get
      if !ignore(mc, methodNode)
    } classCoverage.addMethod(mc)
  }

  private def ignore(mc: IMethodCoverage, node: MethodNode): Boolean = {

    def isModuleStaticInit = isModuleClass && node.name == "<clinit>"

    (
      // equals/hashCode/unapply et al
      isSyntheticMethod(classCoverage.getName, node.name, mc.getFirstLine, mc.getLastLine)
      // static init, `otherwise `case class Foo` reports uncovered code if `object Foo` is not accessed
      || isModuleStaticInit
      || isScalaForwarder(classCoverage.getName, node)
      || isAccessor(node)
    )
  }
}

final class FilteringAnalyzer(executionData: ExecutionDataStore, coverageVisitor: ICoverageVisitor)
    extends Analyzer(executionData, coverageVisitor) {

  private def analyzerError(location: String, cause: Exception): java.io.IOException = {
    val ex = new java.io.IOException(String.format("Error while analyzing %s.", location))
    ex.initCause(cause)
    ex
  }

  override def analyzeClass(buffer: Array[Byte], location: String): Unit = {
    try {
      val reader = InstrSupport.classReaderFor(buffer)
      if ((reader.getAccess() & Opcodes.ACC_MODULE) != 0) {
        return
      }
      if ((reader.getAccess() & Opcodes.ACC_SYNTHETIC) != 0) {
        return
      }
      val classNode = new ClassNode()
      reader.accept(classNode, 0)
      val visitor = createFilteringVisitor(CRC64.classId(buffer), reader.getClassName, classNode)
      reader.accept(visitor, 0)
    } catch {
      case cause: RuntimeException => throw analyzerError(location, cause)
    }
  }

  // override def analyzeClass(reader: ClassReader): Unit = {
  //   val classNode = new ClassNode()
  //   reader.accept(classNode, 0)
  //   val visitor = createFilteringVisitor(CRC64.classId(reader.b), reader.getClassName, classNode)
  //   reader.accept(visitor, 0)
  // }

  private def createFilteringVisitor(classid: Long, className: String, classNode: ClassNode): ClassVisitor = {
    val data = Option(executionData.get(classid))
    val noMatch = data.isEmpty || executionData.contains(className)
    val probes = data.map(_.getProbes).orNull
    val classCoverageAnalyzer = new ClassCoverageImpl(className, classid, noMatch)
    val analyzer =
      new FilteringClassAnalyzer(classCoverageAnalyzer, classNode, probes, new StringPool, coverageVisitor)
    new ClassProbesAdapter(analyzer, false)
  }
}
