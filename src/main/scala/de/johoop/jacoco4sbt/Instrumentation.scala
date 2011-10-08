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

import org.jacoco.core.runtime.LoggerRuntime
import org.jacoco.core.instr.Instrumenter

import java.io.FileInputStream

trait Instrumentation extends Utils with Keys {

  def instrumentAction(compileProducts: Seq[File], testProducts: Seq[File], instrumentedClassDirectory: File, streams: TaskStreams) = {
    streams.log.info("instrumenting products: " + testProducts)

    runtime.shutdown()
    runtime.startup()

    val instrumenter = new Instrumenter(runtime)
    val rebaseClassFiles = Path.rebase(compileProducts, instrumentedClassDirectory)
    
    for { 
      classFile <- (PathFinder(compileProducts) ** "*.class").get
      _ = streams.log.info("instrumenting " + classFile)
      classStream = new FileInputStream(classFile)
      instrumentedClass = try instrumenter.instrument(classStream) finally classStream.close()
    } {
        IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
    }
    Seq(instrumentedClassDirectory) ++ testProducts
  }
}
