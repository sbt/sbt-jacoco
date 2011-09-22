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

  def instrument(implicit buildState: State) = {
    logger(buildState) info "Instrumenting class files."

    runtime.shutdown()
    runtime.startup()
    val instrumenter = new Instrumenter(runtime)
    
    val instrumentedProducts = Seq(Compile, Test, Runtime) map { 
      config => instrumentClasses(instrumenter, config)
    }

    addSettings(instrumentedProducts)
  }
  
  def instrumentClasses(instrumenter: Instrumenter, config: Configuration)(implicit buildState: State) = {
    products in config ~= { original =>
      logger(buildState).info("instrumenting: products in " + config + ": " + original)
      
      val instrumented = getSetting(instrumentedClassDirectory, config).get
      val rebaseClassFiles = Path.rebase(original, instrumented)
      
      for { 
        classFile <- (PathFinder(original) ** "*.class").get
        _ = logger(buildState).info("instrumenting " + classFile)
        classStream = new FileInputStream(classFile)
        instrumentedClass = try instrumenter.instrument(classStream) finally classStream.close()
      } {
          IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
      }
      
      Seq(instrumented)
    }
  }
}
