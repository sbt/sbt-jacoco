/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2014 Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.johoop.jacoco4sbt

object Util {
  def processName = if (System.getProperty("os.name").contains("Windows")) "sbt.bat" else "sbt"
}
