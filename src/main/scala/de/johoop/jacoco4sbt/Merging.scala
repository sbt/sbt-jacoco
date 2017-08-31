/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011-2013 Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.johoop.jacoco4sbt

import java.io._

import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.tools.ExecFileLoader
import sbt.Keys._
import sbt._

trait Merging extends JaCoCoRuntime {
  def conditionalMergeAction(
      utExecutionData: File,
      itExecutionData: File,
      mergedExecutionData: File,
      mergeReports: Boolean,
      streams: TaskStreams): Unit = {

    if (mergeReports) {
      mergeAction(utExecutionData, itExecutionData, mergedExecutionData, streams)
    } else {
      streams.log.debug("Not merging execution data")
    }
  }

  def mergeAction(
      utExecutionData: File,
      itExecutionData: File,
      mergedExecutionData: File,
      streams: TaskStreams): Unit = {

    val sources = Seq(utExecutionData, itExecutionData).filter(_.exists())
    streams.log debug ("Found data files: %s" format sources.map(_.absolutePath).mkString(", "))

    val loader = new ExecFileLoader
    sources foreach loader.load

    streams.log debug ("Writing merged data to: %s" format mergedExecutionData.getAbsolutePath)

    writeToFile(mergedExecutionData) { outputStream =>
      val dataWriter = new ExecutionDataWriter(outputStream)
      loader.getSessionInfoStore accept dataWriter
      loader.getExecutionDataStore accept dataWriter
    }
  }

  private def writeToFile(f: File)(writeFn: OutputStream => Unit): Unit = {
    try {
      val out = new BufferedOutputStream(new FileOutputStream(f))
      try writeFn(out)
      catch {
        case e: IOException => sys.error("Error merging Jacoco files: %s" format e.getMessage)
      } finally out.close
    } catch {
      case e: IOException => 
        sys.error("Unable to write out Jacoco file during merge: %s" format e.getMessage)
    }
  }
}
