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

import sbt.Def
import sbt.Keys.Classpath

private[jacoco] object JacocoPluginCompat {
  def testFull = sbt.Keys.test

  def convertClasspath(x: Classpath): Classpath =
    x

  def fromClasspath(x: Classpath): Classpath =
    x

  implicit class DefOps(private val self: Def.type) extends AnyVal {
    def uncached[A](a: A): A = a
  }
}
