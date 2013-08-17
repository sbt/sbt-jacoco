/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011-2013 Joachim Hofer & contributors
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

import org.jacoco.core.runtime.{RuntimeData, LoggerRuntime}
import org.jacoco.core.instr.Instrumenter

import java.io.FileInputStream

trait Instrumentation extends JaCoCoRuntime {

  def instrumentAction(compileProducts: Seq[File], fullClasspath: Classpath, instrumentedClassDirectory: File, 
      streams: TaskStreams) = {
    
    streams.log debug ("instrumenting products: " + compileProducts)

    runtime.shutdown
    runtime startup runtimeData

    val instrumenter = new Instrumenter(runtime)
    val rebaseClassFiles = Path.rebase(compileProducts, instrumentedClassDirectory)
    
    for { 
      classFile <- (PathFinder(compileProducts) ** "*.class").get
      _ = streams.log debug ("instrumenting " + classFile)
      classStream = new FileInputStream(classFile)
      instrumentedClass = try instrumenter instrument (classStream, classFile.name) finally classStream.close
    } {
        IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
    }
    
    Attributed.blank(instrumentedClassDirectory) +: fullClasspath 
  }
}
