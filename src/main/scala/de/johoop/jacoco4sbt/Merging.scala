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

import sbt._

trait Merging extends JaCoCoRuntime {
  import org.jacoco.core.data.{ExecutionDataWriter, ExecFileLoader}
  import java.io._
  import sbt.Keys._

  def conditionalMergeAction(itJacocoDirectory: File, jacocoDirectory: File, streams: TaskStreams, mergeReports: Boolean) = {
    if (mergeReports) mergeAction(itJacocoDirectory, jacocoDirectory, streams)
    else streams.log debug "Not merging execution data!"
  }

  def mergeAction(itJacocoDirectory: File, jacocoDirectory: File, streams: TaskStreams) = {
    val parent = jacocoDirectory.getParentFile

    val execs = (jacocoDirectory / "jacoco.exec" +++ itJacocoDirectory / "jacoco.exec").get
    streams.log debug ("Found data files: %s" format execs.map(_.absolutePath).mkString(", "))

    val loader = new ExecFileLoader
    execs foreach loader.load

    val mergedFile = new File(itJacocoDirectory, "jacoco-merged.exec")
    streams.log debug ("Writing merged data to: %s" format mergedFile.getAbsolutePath)

    writeToFile(mergedFile) { outputStream =>
      val dataWriter = new ExecutionDataWriter(outputStream)
      loader.getSessionInfoStore accept dataWriter
      loader.getExecutionDataStore accept dataWriter
    }
  }

  private def writeToFile(f: File)(writeFn: OutputStream => Unit) = {
    try {
      val out = new BufferedOutputStream(new FileOutputStream(f))
      try writeFn(out)
      catch {
        case e: IOException => throw new ResourcesException("Error merging Jacoco files: %s" format e.getMessage)
      } finally out.close
    } catch {
      case e: IOException => 
        throw new ResourcesException("Unable to write out Jacoco file during merge: %s" format e.getMessage)
    }
  }
}
