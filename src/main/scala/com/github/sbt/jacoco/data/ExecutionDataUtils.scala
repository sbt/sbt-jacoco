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

import java.io.FileOutputStream

import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.tools.ExecFileLoader
import resource._
import sbt.Keys.TaskStreams
import sbt._

object ExecutionDataUtils {
  def saveRuntimeData(data: ProjectData, destination: File, forked: Boolean, streams: TaskStreams): Unit = {
    if (!forked) {
      streams.log.debug(s"writing execution data to $destination")
      IO.createDirectory(destination.getParentFile)

      for {
        os <- managed(new FileOutputStream(destination))
      } {
        val executionDataWriter = new ExecutionDataWriter(os)
        data.data.collect(executionDataWriter, executionDataWriter, true)
      }
    }
  }

  def mergeExecutionData(sources: Seq[File], destination: File, streams: TaskStreams): Unit = {
    val files = sources.filter(_.exists())
    streams.log.debug(s"Found data files: ${files.map(_.absolutePath).mkString(", ")}")

    val loader = new ExecFileLoader()
    files.foreach(loader.load)

    streams.log.debug(s"Writing merged data to: ${destination.getAbsolutePath}")

    IO.createDirectory(destination.getParentFile)

    for {
      os <- managed(new FileOutputStream(destination))
    } {
      val dataWriter = new ExecutionDataWriter(os)
      loader.getSessionInfoStore.accept(dataWriter)
      loader.getExecutionDataStore.accept(dataWriter)
    }
  }
}
