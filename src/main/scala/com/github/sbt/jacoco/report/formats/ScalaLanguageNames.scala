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

package com.github.sbt.jacoco.report.formats

import org.jacoco.report.JavaNames
import com.github.sbt.jacoco.filter.ScalaForwarderDetector

import scala.reflect.NameTransformer._

private[formats] class ScalaLanguageNames extends JavaNames {
  override def getPackageName(vmname: String): String =
    super.getPackageName(decode(vmname))

  override def getClassName(
      vmname: String,
      vmsignature: String,
      vmsuperclass: String,
      vminterfaces: Array[String]): String = {
    if (vmname.contains("anonfun$")) {
      vmname.split("""anonfun\$""").toList match {
        case List(pre, post) =>
          getClassName(cleanClassName(pre)) + " anonfun$" + post
        case _ =>
          getClassName(cleanClassName(vmname))
      }
    } else if (vmname.contains("$anon$")) {
      vminterfaces.map(getClassName).mkString("new " + getClassName(vmsuperclass), " with ", "{ ... }")
    } else {
      getClassName(cleanClassName(vmname))
    }
  }

  override def getQualifiedClassName(vmname: String): String =
    super.getQualifiedClassName(cleanClassName(vmname))

  override def getMethodName(vmclassname: String, vmmethodname: String, vmdesc: String, vmsignature: String): String =
    super.getMethodName(vmclassname, getMethodName(vmmethodname), vmdesc, vmsignature)

  override def getQualifiedMethodName(
      vmclassname: String,
      vmmethodname: String,
      vmdesc: String,
      vmsignature: String): String =
    super.getQualifiedMethodName(vmclassname, getMethodName(vmmethodname), vmdesc, vmsignature)

  private def cleanClassName(name: String) = {
    decode(name.stripSuffix("$class"))
  }

  private def getClassName(vmname: String) = {
    val pos: Int = vmname.lastIndexOf('/')
    val name: String = if (pos == -1) vmname else vmname.substring(pos + 1)
    cleanClassName(
      if (name.endsWith("$$")) {
        name.dropRight(2).replace('$', '.') // ambiguous, we could be an inner class of the object or the class.
      } else if (name.endsWith("$")) {
        name.dropRight(1).replace('$', '.') + " (object)"
      } else {
        name.replace('$', '.')
      }
    )
  }

  private def getMethodName(vmname: String) = {
    val pos: Int = vmname.lastIndexOf("$$")
    val name: String = if (pos == -1) vmname else vmname.substring(pos + 2)
    decode(name.stripSuffix(ScalaForwarderDetector.LazyComputeSuffix))
  }
}
