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

package org.scalasbt.jacoco

import org.jacoco.core.runtime.{LoggerRuntime, RuntimeData}

private[jacoco] trait JaCoCoRuntime {
  val runtimeData = new RuntimeData
  val runtime = new LoggerRuntime
}
