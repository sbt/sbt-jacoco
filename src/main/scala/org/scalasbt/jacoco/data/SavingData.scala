package org.scalasbt.jacoco.data

import java.io.FileOutputStream

import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.runtime.RuntimeData
import resource._
import sbt.Keys.TaskStreams
import sbt.{File, IO}

object SavingData {
  def saveDataAction(jacocoFile: File, forked: Boolean, streams: TaskStreams)(
      implicit runtimeData: RuntimeData): Unit = {

    if (!forked) {
      streams.log.debug(s"writing execution data to $jacocoFile")
      IO.createDirectory(jacocoFile.getParentFile)

      for (os <- managed(new FileOutputStream(jacocoFile))) {
        val executionDataWriter = new ExecutionDataWriter(os)
        runtimeData.collect(executionDataWriter, executionDataWriter, true)
      }
    }
  }
}
