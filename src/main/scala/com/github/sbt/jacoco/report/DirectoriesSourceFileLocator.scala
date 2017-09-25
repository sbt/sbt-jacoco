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

import java.io.{File, Reader}

import org.jacoco.report.{DirectorySourceFileLocator, ISourceFileLocator}

class DirectoriesSourceFileLocator(directories: Seq[File], sourceSettings: JacocoSourceSettings)
    extends ISourceFileLocator {

  override def getSourceFile(packageName: String, fileName: String): Reader = {
    def findInDirectory(dir: File) = Option(dirSourceLocator(dir).getSourceFile(packageName, fileName))
    def dirSourceLocator(dir: File) =
      new DirectorySourceFileLocator(dir, sourceSettings.fileEncoding, sourceSettings.tabWidth)

    (directories flatMap findInDirectory).headOption.orNull
  }

  override def getTabWidth: Int = sourceSettings.tabWidth
}
