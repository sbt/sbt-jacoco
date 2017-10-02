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

import org.jacoco.core.runtime.{IRuntime, LoggerRuntime, RuntimeData}

import scala.collection.concurrent.TrieMap

object ProjectData {
  private val data: TrieMap[String, ProjectData] = TrieMap()

  def apply(projectId: String): ProjectData = {
    data.getOrElseUpdate(projectId, ProjectData(new RuntimeData(), new LoggerRuntime()))
  }
}

case class ProjectData(data: RuntimeData, runtime: IRuntime)
