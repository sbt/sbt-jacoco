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

package com.github.sbt.jacoco.report

case class JacocoThresholds(
    instruction: Double = 0,
    method: Double = 0,
    branch: Double = 0,
    complexity: Double = 0,
    line: Double = 0,
    clazz: Double = 0)
