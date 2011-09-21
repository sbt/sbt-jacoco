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

trait Instrumentation extends Utils with Keys {
  def instrument(implicit buildState: State) = {
    import org.jacoco.core.runtime.LoggerRuntime
    import org.jacoco.core.instr.Instrumenter
    import java.io.FileInputStream
    
    logger(buildState) info "Instrumenting class files."

    val classes = Project.evaluateTask(classDirectories in Config, buildState).get.toEither.right.get // TODO error handling?
    val jacocoRuntime = getSetting(runtime).get getOrElse new LoggerRuntime
    
    jacocoRuntime.reset()
    val withRuntime = addSetting(runtime in Config := Some(jacocoRuntime))
    
    doInJacocoDirectory { jacocoDirectory =>
      val instrumenter = new Instrumenter(jacocoRuntime)

      for {
        baseDirectory <- classes
        rebaseClassFiles = Path.rebase(baseDirectory, jacocoDirectory / "instrumented-classes" )
        classFile <- (baseDirectory ** "*.class").get
        _ = logger(buildState).debug("instrumenting " + classFile)
        classStream = new FileInputStream(classFile)
        instrumentedClass = try instrumenter.instrument(classStream) finally classStream.close()
      } {
        IO.write(rebaseClassFiles(classFile).get, instrumentedClass)
      }
      
      withRuntime
    }
  }
}