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

package org.scalasbt.jacoco.data

import java.io.FileInputStream

import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.{IRuntime, OfflineInstrumentationAccessGenerator, RuntimeData}
import resource._
import sbt.Keys._
import sbt._

object InstrumentationUtils {

  def instrumentClasses(
      classFiles: Seq[File],
      classpath: Classpath,
      destDirectory: File,
      updateReport: UpdateReport,
      forked: Boolean,
      runtime: IRuntime,
      runtimeData: RuntimeData,
      streams: TaskStreams): Seq[Attributed[File]] = {

    val classCount = classFiles.foldLeft(0) { (acc, p) =>
      acc + (p ** "*.class").get.size
    }

    streams.log.info(s"Instrumenting $classCount classes to $destDirectory")
    streams.log.debug(s"instrumenting products: ${classFiles.mkString(",")}")

    val (jacocoAgent, instrumenter) = if (forked) {
      val agentJars = updateReport.select(module = moduleFilter(name = "org.jacoco.agent"))

      val tmp = IO.temporaryDirectory
      val jacocoAgent = agentJars
        .flatMap(IO.unzip(_, tmp, (_: String).endsWith(".jar")))
        .map(Attributed.blank)

      streams.log.debug(s"Found jacoco agent: $jacocoAgent")
      (jacocoAgent, new Instrumenter(new OfflineInstrumentationAccessGenerator))
    } else {
      runtime.shutdown()
      runtime.startup(runtimeData)
      (Nil, new Instrumenter(runtime))
    }

    val rebaseClassFiles = Path.rebase(classFiles, destDirectory)

    for {
      classFile <- (PathFinder(classFiles) ** "*.class").get
      classStream <- managed(new FileInputStream(classFile))
    } {
      streams.log.debug(s"instrumenting $classFile")
      val instrumentedClass = instrumenter.instrument(classStream, classFile.name)
      IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
    }

    jacocoAgent ++ (Attributed.blank(destDirectory) +: classpath)
  }
}
