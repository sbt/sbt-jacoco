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

package org.scalasbt.jacoco

import java.io.FileInputStream

import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.{IRuntime, OfflineInstrumentationAccessGenerator, RuntimeData}
import sbt.Keys._
import sbt._

private[jacoco] object Instrumentation {

  def instrumentAction(
      compileProducts: Seq[File],
      fullClasspath: Classpath,
      instrumentedClassDirectory: File,
      resolved: UpdateReport,
      forked: Boolean,
      streams: TaskStreams)(implicit runtime: IRuntime, runtimeData: RuntimeData): Seq[Attributed[File]] = {

    streams.log debug s"instrumenting products: $compileProducts"

    val (jacocoAgent, instrumenter) = if (forked) {
      val agentJars = resolved select (module = moduleFilter(name = "org.jacoco.agent"))

      val tmp = IO.temporaryDirectory
      val jacocoAgent = agentJars flatMap (IO.unzip(_, tmp, (_: String) endsWith ".jar")) map Attributed.blank
      streams.log debug s"Found jacoco agent: $jacocoAgent"
      (jacocoAgent, new Instrumenter(new OfflineInstrumentationAccessGenerator))

    } else {
      runtime.shutdown()
      runtime.startup(runtimeData)
      (Seq(), new Instrumenter(runtime))
    }

    val rebaseClassFiles = Path.rebase(compileProducts, instrumentedClassDirectory)

    for {
      classFile <- (PathFinder(compileProducts) ** "*.class").get
      _ = streams.log debug ("instrumenting " + classFile)
      classStream = new FileInputStream(classFile)
      instrumentedClass = try {
        instrumenter instrument (classStream, classFile.name)
      } finally {
        classStream.close()
      }
    } {
      IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
    }

    jacocoAgent ++ (Attributed.blank(instrumentedClassDirectory) +: fullClasspath)
  }
}
