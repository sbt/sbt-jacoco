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

package de.johoop.jacoco4sbt.filter

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
    if ((access & Opcodes.ACC_SYNTHETIC) != 0)
      null
    else {
      new MethodAnalyzer(stringPool.get(name), stringPool.get(desc), stringPool.get(signature), probes) {
        override def visitEnd(): Unit = {
          super.visitEnd()
          val hasInstructions = getCoverage.getInstructionCounter.getTotalCount > 0
          if (hasInstructions)
            coverages += getCoverage
        }
      }
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
    import AccessorDetector._
    import ScalaForwarderDetector._
    import ScalaSyntheticMethod._
    import node.name
    def isModuleStaticInit = isModuleClass && name == "<clinit>"

    (
      isSyntheticMethod(classCoverage.getName, name, mc.getFirstLine, mc.getLastLine) // equals/hashCode/unapply et al
      || isModuleStaticInit // static init, `otherwise `case class Foo` reports uncovered code if `object Foo` is not accessed
      || isScalaForwarder(classCoverage.getName, node)
      || isAccessor(node)
    )
  }
}

final class FilteringAnalyzer(executionData: ExecutionDataStore, coverageVisitor: ICoverageVisitor)
    extends Analyzer(executionData, coverageVisitor) {

  override def analyzeClass(reader: ClassReader): Unit = {
    val classNode = new ClassNode()
    reader.accept(classNode, 0)
    val visitor = createFilteringVisitor(CRC64.checksum(reader.b), reader.getClassName, classNode)
    reader.accept(visitor, 0)
  }

  private def createFilteringVisitor(classid: Long, className: String, classNode: ClassNode): ClassVisitor = {
    val data = Option(executionData get classid)
    val (noMatch, probes) = data
      .map(data => (false, data.getProbes))
      .getOrElse((executionData contains className, null))
    val classCoverageAnalyzer = new ClassCoverageImpl(className, classid, noMatch)
    val analyzer =
      new FilteringClassAnalyzer(classCoverageAnalyzer, classNode, probes, new StringPool, coverageVisitor)
    new ClassProbesAdapter(analyzer, false)
  }
}
