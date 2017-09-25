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

object ScalaSyntheticMethod {
  def isSyntheticMethod(owner: String, name: String, firstLine: Int, lastLine: Int): Boolean = {
    val isModuleClass = owner.endsWith("$")
    val isOneLiner = firstLine == lastLine
    isOneLiner && (
      (isModuleClass && isSyntheticObjectMethodName(name))
      || isSyntheticInstanceMethodName(name)
    )
  }

  private def isSyntheticInstanceMethodName(name: String): Boolean = isCaseInstanceMethod(name)
  private def isSyntheticObjectMethodName(name: String): Boolean =
    isCaseCompanionMethod(name) || isAnyValCompanionMethod(name)

  private def isCaseInstanceMethod(name: String) = name match {
    case "canEqual" | "copy" | "equals" | "hashCode" | "productPrefix" | "productArity" | "productElement" |
        "productIterator" | "toString" =>
      true
    case _ if name.startsWith("copy$default") => true
    case _ => false
  }
  private def isCaseCompanionMethod(name: String) = name match {
    case "apply" | "unapply" | "unapplySeq" | "readResolve" => true
    case _ => false
  }
  private def isAnyValCompanionMethod(name: String) = name match {
    case "equals$extension" | "hashCode$extension" => true
    case _ => false
  }
}
