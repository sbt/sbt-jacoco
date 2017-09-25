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

import org.objectweb.asm.Opcodes._
import org.objectweb.asm.tree.{JumpInsnNode, MethodInsnNode, MethodNode}

import scala.collection.JavaConverters._

/** Detects forwarder methods added by Scala
  *  - classes and objects that mix in traits have a forwarder to the method body
  *    in the trait implementation class
  *  - classes contains static forwarders to methods in the companion object (for convenient Java interop)
  *  - methods in (boxed) value classes forward to the method body in the companion object
  *  - implicit classes creates a factory method beside the class.
  *  - lazy vals have an accessor that forwards to `\$lzycompute`, which is the method
  *    with the interesting code.
  */
object ScalaForwarderDetector {
  val LazyComputeSuffix: String = "$lzycompute"
  def isScalaForwarder(className: String, node: MethodNode): Boolean = {
    if (node.instructions.size() <= 100) {
      val insn = node.instructions.iterator().asScala.toList
      val hasJump = insn.exists {
        case _: JumpInsnNode => true
        case _ => false
      }
      val hasForwarderCall = insn.exists {
        case minsn: MethodInsnNode =>
          isScalaForwarder(className, node.name, minsn.getOpcode, minsn.owner, minsn.name, minsn.desc, hasJump)
        case _ => false
      }
      hasForwarderCall
    } else {
      false
    }
  }

  def isScalaForwarder(
      className: String,
      methodName: String,
      opcode: Int,
      calledMethodOwner: String,
      calledMethodName: String,
      desc: String,
      hasJump: Boolean): Boolean = {
    val callingCompanionModule = calledMethodOwner == (className + "$")
    val callingImplClass = calledMethodOwner.endsWith("$class")
    val callingImplicitClass = calledMethodOwner.endsWith("$" + methodName) || calledMethodOwner == methodName
    val extensionName = methodName + "$extension"

    val staticForwarder = opcode == INVOKEVIRTUAL && callingCompanionModule && calledMethodName == methodName
    val traitForwarder = opcode == INVOKESTATIC && callingImplClass && calledMethodName == methodName
    val extensionMethodForwarder = opcode == INVOKEVIRTUAL &&
      callingCompanionModule &&
      calledMethodName == extensionName
    val implicitClassFactory = opcode == INVOKESPECIAL && callingImplicitClass && calledMethodName == "<init>"
    val lazyAccessor = opcode == INVOKESPECIAL && calledMethodName.endsWith(LazyComputeSuffix)
    val forwards = (
      (staticForwarder || traitForwarder || extensionMethodForwarder || implicitClassFactory) &&
        !hasJump // second condition a sanity check
        || lazyAccessor
    )
    forwards
  }
}
