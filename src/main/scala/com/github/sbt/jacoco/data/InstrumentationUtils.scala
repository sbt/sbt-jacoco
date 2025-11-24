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

package com.github.sbt.jacoco.data

import java.io.FileInputStream

import org.jacoco.core.instr.Instrumenter
import org.jacoco.core.runtime.OfflineInstrumentationAccessGenerator
import sbt.Keys.*
import sbt.*
import scala.util.Using

object InstrumentationUtils {

  def instrumentClasses(
      products: Seq[File],
      classFiles: Seq[File],
      classpath: Seq[Attributed[File]],
      destDirectory: File,
      updateReport: UpdateReport,
      forked: Boolean,
      projectData: ProjectData,
      streams: TaskStreams
  ): Seq[Attributed[File]] = {

    streams.log.info(s"Instrumenting ${classFiles.size} classes to $destDirectory")
    streams.log.debug(s"instrumenting products: ${products.mkString(",")}")

    val (jacocoAgent, instrumenter) = if (forked) {
      val agentJars = updateReport.select(module = moduleFilter(name = "org.jacoco.agent"))

      val tmp = IO.temporaryDirectory
      val jacocoAgent = agentJars
        .flatMap(IO.unzip(_, tmp, (_: String).endsWith(".jar")))
        .map(Attributed.blank)

      streams.log.debug(s"Found jacoco agent: $jacocoAgent")
      (jacocoAgent, new Instrumenter(new OfflineInstrumentationAccessGenerator))
    } else {
      projectData.runtime.shutdown()
      projectData.runtime.startup(projectData.data)
      (Nil, new Instrumenter(projectData.runtime))
    }

    val rebaseClassFiles = Path.rebase(products, destDirectory)

    for {
      classFile <- classFiles
    } {
      Using.resource(new FileInputStream(classFile)) { classStream =>
        streams.log.debug(s"instrumenting $classFile")
        val instrumentedClass = instrumenter.instrument(classStream, classFile.name)
        IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
      }
    }

    jacocoAgent ++ (Attributed.blank(destDirectory) +: classpath)
  }
}
