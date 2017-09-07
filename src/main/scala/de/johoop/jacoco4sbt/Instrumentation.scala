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

package de.johoop.jacoco4sbt

import sbt._
import Keys._

import org.jacoco.core.instr.Instrumenter

import java.io.FileInputStream
import org.jacoco.core.runtime.OfflineInstrumentationAccessGenerator

private[jacoco4sbt] trait Instrumentation extends JaCoCoRuntime {

  def instrumentAction(
      compileProducts: Seq[File],
      fullClasspath: Classpath,
      instrumentedClassDirectory: File,
      resolved: UpdateReport,
      forked: Boolean,
      streams: TaskStreams): Seq[Attributed[File]] = {

    streams.log debug s"instrumenting products: $compileProducts"

    val (jacocoAgent, instrumenter) = if (forked) {
      val agentJars = resolved select (module = moduleFilter(name = "org.jacoco.agent"))

      val tmp = IO.temporaryDirectory
      val jacocoAgent = agentJars flatMap (IO.unzip(_, tmp, (_: String) endsWith ".jar")) map Attributed.blank
      streams.log debug s"Found jacoco agent: $jacocoAgent"
      (jacocoAgent, new Instrumenter(new OfflineInstrumentationAccessGenerator))

    } else {
      runtime.shutdown()
      runtime startup runtimeData
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
