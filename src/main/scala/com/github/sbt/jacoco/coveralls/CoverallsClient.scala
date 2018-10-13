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

package com.github.sbt.jacoco.coveralls

import java.io.{File, FileInputStream}

import sbt.Keys.TaskStreams

import scalaj.http.{Http, MultiPart}

object CoverallsClient {
  private val jobsUrl = "https://coveralls.io/api/v1/jobs"

  def sendReport(reportFile: File, streams: TaskStreams): Unit = {
    streams.log.info("Uploading coverage to coveralls.io...")
    val response = Http(jobsUrl)
      .postMulti(
        MultiPart(
          "json_file",
          "json_file.json",
          "application/json",
          new FileInputStream(reportFile),
          reportFile.length(),
          _ => ())
      )
      .asString

    if (response.isSuccess) {
      streams.log.info("Upload complete")
    } else {
      streams.log.error(s"Unexpected response from coveralls: ${response.code}")
    }
  }
}
