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

import sbt.Keys.*
import java.io.File
import sbt.internal.util.Attributed

private[jacoco] object JacocoPluginCompat {
  def testFull = sbt.Keys.testFull

  inline def convertClasspath(x: Seq[Attributed[File]]): Classpath = {
    val converter = fileConverter.value
    x.map(_.map(f => converter.toVirtualFile(f.toPath)))
  }

  inline def fromClasspath(x: Classpath): Seq[Attributed[File]] = {
    val converter = fileConverter.value
    x.map(_.map(f => converter.toPath(f).toFile))
  }
}
