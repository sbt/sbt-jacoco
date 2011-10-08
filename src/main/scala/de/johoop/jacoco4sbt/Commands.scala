/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011 Joachim Hofer
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
import CommandSupport.logger

trait Commands extends Keys with Instrumentation with Utils {
  def persistCoverageData(implicit buildState: State) = {
    import java.io.FileOutputStream
    import org.jacoco.core.data.ExecutionDataWriter

    doInJacocoDirectory { jacocoDirectory =>
      IO createDirectory jacocoDirectory
      val executionDataStream = new FileOutputStream(jacocoDirectory / "jacoco.exec")
      try {
        logger(buildState) info "writing execution data to " + jacocoDirectory / "jacoco.exec"
        val executionDataWriter = new ExecutionDataWriter(executionDataStream)
        runtime.collect(executionDataWriter, null, true)
        executionDataStream.flush()
      } finally {
        executionDataStream.close()
      }

      buildState
    }
  }
}