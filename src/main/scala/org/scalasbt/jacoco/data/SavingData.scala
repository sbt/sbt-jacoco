package org.scalasbt.jacoco.data

import java.io.FileOutputStream

import org.jacoco.core.data.ExecutionDataWriter
import org.jacoco.core.runtime.RuntimeData
import sbt.{File, IO}
import sbt.Keys.TaskStreams

object SavingData {
  def saveDataAction(jacocoFile: File, forked: Boolean, streams: TaskStreams)(
      implicit runtimeData: RuntimeData): Unit = {

    if (!forked) {
      IO createDirectory jacocoFile.getParentFile
      val executionDataStream = new FileOutputStream(jacocoFile)
      try {
        streams.log debug ("writing execution data to " + jacocoFile)
        val executionDataWriter = new ExecutionDataWriter(executionDataStream)
        runtimeData collect (executionDataWriter, executionDataWriter, true)
        executionDataStream.flush()
      } finally {
        executionDataStream.close()
      }
    }
  }
}
