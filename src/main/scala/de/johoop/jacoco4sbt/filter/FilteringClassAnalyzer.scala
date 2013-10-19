/*
 * This file is part of jacoco4sbt.
 *
 * Copyright (c) 2013 Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.johoop.jacoco4sbt.filter

import scala.collection.mutable
import org.jacoco.core.internal.analysis.{MethodAnalyzer, StringPool, ClassAnalyzer}
import org.jacoco.core.internal.flow.{ClassProbesAdapter, MethodProbesVisitor}
import org.jacoco.core.internal.instr.InstrSupport
import org.objectweb.asm._
import org.jacoco.core.analysis.{Analyzer, ICoverageVisitor, IMethodCoverage}
import org.jacoco.core.internal.data.CRC64
import org.jacoco.core.data.ExecutionDataStore
import org.objectweb.asm.tree.{JumpInsnNode, MethodInsnNode, MethodNode, ClassNode}
import scala.collection.JavaConverters._

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
private final class FilteringClassAnalyzer(classid: Long, classNode: ClassNode, probes: Array[Boolean],
                                           stringPool: StringPool, coverageVisitor: ICoverageVisitor) extends ClassAnalyzer(classid, probes, stringPool) {

  private val className = classNode.name

  private val coverages = mutable.Buffer[IMethodCoverage]()

  override def visitMethod(access: Int, name: String, desc: String,
                           signature: String, exceptions: Array[String]): MethodProbesVisitor = {
    InstrSupport.assertNotInstrumented(name, getCoverage.getName)
    if ((access & Opcodes.ACC_SYNTHETIC) != 0)
      null
    else {
      new MethodAnalyzer(stringPool.get(name), stringPool.get(desc), stringPool.get(signature), probes) {
        override def visitEnd() {
          super.visitEnd()
          val hasInstructions = getCoverage.getInstructionCounter.getTotalCount > 0
          if (hasInstructions)
            coverages += getCoverage
        }
      }
    }
  }

  override def visitEnd() {
    try visitFiltered()
    finally {
      super.visitEnd()
      coverageVisitor.visitCoverage(getCoverage)
    }
  }

  private val isModuleClass = className.endsWith("$")

  private val methods: Seq[MethodNode] = classNode.methods.asInstanceOf[java.util.List[MethodNode]].asScala

  private def visitFiltered() {
    for {
      mc <- coverages
      methodNode = methods.find(m => m.name == mc.getName && m.desc == mc.getDesc).get
      if !ignore(mc, methodNode)
    } getCoverage.addMethod(mc)
  }

  private def ignore(mc: IMethodCoverage, node: MethodNode): Boolean = {
    import node.name
    import ScalaSyntheticMethod._, ScalaForwarderDetector._, AccessorDetector._
    def isModuleStaticInit = isModuleClass && name == "<clinit>"

    (
         isSyntheticMethod(className, name, mc.getFirstLine, mc.getLastLine)  // equals/hashCode/unapply et al
      || isModuleStaticInit // static init, `otherwise `case class Foo` reports uncovered code if `object Foo` is not accessed
      || isScalaForwarder(className, node)
      || isAccessor(node)
    )
  }
}

final class FilteringAnalyzer(executionData: ExecutionDataStore,
                              coverageVisitor: ICoverageVisitor) extends Analyzer(executionData, coverageVisitor) {
  override def analyzeClass(reader: ClassReader) {
    val classNode = new ClassNode()
    reader.accept(classNode, 0)
    val visitor = createFilteringVisitor(CRC64.checksum(reader.b), classNode)
    reader.accept(visitor, 0)
  }

  private def createFilteringVisitor(classid: Long, classNode: ClassNode): ClassVisitor = {
    val data = executionData.get(classid)
    val probes = if (data == null) null else data.getProbes
    val stringPool = new StringPool
    val analyzer = new FilteringClassAnalyzer(classid, classNode, probes, stringPool, coverageVisitor)
    new ClassProbesAdapter(analyzer)
  }
}
