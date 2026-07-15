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

package com.github.sbt.jacoco.report.formats

import com.github.sbt.jacoco.report.DirectoriesSourceFileLocator
import org.jacoco.core.analysis.{IBundleCoverage, IClassCoverage, IMethodCoverage, IPackageCoverage}
import org.jacoco.core.data.{ExecutionData, SessionInfo}
import org.jacoco.report.{IReportGroupVisitor, IReportVisitor, ISourceFileLocator, JavaNames}
import sbt.IO

import java.io.*
import java.util
import scala.collection.mutable

class CoberturaReportFormat extends JacocoReportFormat {
  override def createVisitor(directory: File, encoding: String): IReportVisitor = {
    val covDirectory = new File(directory, "coverage-report")
    IO.createDirectory(covDirectory)
    val formatter = new CoberturaFormatter()
    formatter.createVisitor(new FileOutputStream(new File(covDirectory, "cobertura.xml")))
  }
}

class CoberturaRootVisitor(writer: Writer) extends IReportVisitor {

  private var indent = 0
  private var groupVisitor: IReportGroupVisitor = _
  private var sessionInfos: java.util.List[SessionInfo] = new java.util.LinkedList[SessionInfo]()
  private val javaNames = new JavaNames()

  writer.write("<?xml version=\"1.0\"?>\n")
  writer.write("<!DOCTYPE coverage SYSTEM \"http://cobertura.sourceforge.net/xml/coverage-04.dtd\">\n")

  override def visitInfo(
      sessionInfoList: java.util.List[SessionInfo],
      collection: java.util.Collection[ExecutionData]
  ): Unit = {
    this.sessionInfos = sessionInfoList
  }

  override def visitEnd(): Unit = writer.close()

  private val noCallback: () => Unit = () => ()

  /**
   * Write tag into output
   *
   * @param tag
   *   tag name
   * @param attributes
   *   tag attributes
   * @param callback
   *   callback to process nested tags
   */
  private def writeTag(tag: String, attributes: Map[String, Any], callback: () => Unit = noCallback): Unit = {
    writer.write(
      (" " * indent) + s"<$tag" + attributes
        .map(i => s""" ${i._1}="${i._2}"""")
        .mkString + (if (callback == noCallback) "/" else "") + ">\n"
    )
    if (callback != noCallback) {
      indent += 4
      callback()
      indent -= 4
      writer.write((" " * indent) + s"</$tag>\n")
    }
  }

  private def writePackages(packages: util.Collection[IPackageCoverage]): Unit = {
    writeTag(
      "packages",
      Map.empty,
      () =>
        packages.forEach { p =>
          writeTag(
            "package",
            Map(
              "name" -> javaNames.getPackageName(p.getName),
              "line-rate" -> p.getLineCounter.getCoveredRatio,
              "branch-rate" -> p.getBranchCounter.getCoveredRatio,
              "complexity" -> p.getComplexityCounter.getCoveredRatio
            ),
            () => writeClasses(p.getClasses)
          )
        }
    )
  }

  private def writeClasses(classes: util.Collection[IClassCoverage]): Unit = {
    writeTag(
      "classes",
      Map.empty,
      () =>
        classes.forEach { cl =>
          writeTag(
            "class",
            Map(
              "name" -> javaNames.getClassName(cl.getName, cl.getSignature, cl.getSuperName, cl.getInterfaceNames),
              "filename" -> s"""${cl.getPackageName}/${cl.getSourceFileName}""",
              "line-rate" -> cl.getLineCounter.getCoveredRatio,
              "branch-rate" -> cl.getBranchCounter.getCoveredRatio,
              "complexity" -> cl.getComplexityCounter.getCoveredRatio
            ),
            () => writeMethods(cl.getName, cl.getMethods)
          )
        }
    )
  }

  private def writeMethods(className: String, methods: util.Collection[IMethodCoverage]): Unit = {
    writeTag(
      "methods",
      Map.empty,
      () =>
        methods.forEach { m =>
          writeTag(
            "method",
            Map(
              "name" -> javaNames.getMethodName(className, m.getName, m.getDesc, m.getSignature),
              "signature" -> m.getSignature,
              "line-rate" -> m.getLineCounter.getCoveredRatio,
              "branch-rate" -> m.getBranchCounter.getCoveredRatio,
              "complexity" -> m.getComplexityCounter.getCoveredRatio
            ),
            () => writeLines(m)
          )
        }
    )
  }

  private def writeLines(m: IMethodCoverage): Unit = {
    writeTag(
      "lines",
      Map.empty,
      () =>
        for (nr <- m.getFirstLine to m.getLastLine) {
          val line = m.getLine(nr)
          if (line.getStatus >= 1) {
            writeTag(
              "line",
              Map(
                "number" -> nr,
                "hits" -> (if (line.getStatus == 1) 0 else 1),
                "branch" -> (line.getBranchCounter.getStatus == 1)
              )
            )
          }
        }
    )
  }

  override def visitBundle(bundle: IBundleCoverage, locator: ISourceFileLocator): Unit = {
    writeTag(
      "coverage",
      Map(
        "line-rate" -> bundle.getLineCounter.getCoveredRatio,
        "lines-valid" -> bundle.getLineCounter.getTotalCount,
        "lines-covered" -> bundle.getLineCounter.getCoveredCount,
        "branches-valid" -> bundle.getBranchCounter.getTotalCount,
        "branches-covered" -> bundle.getBranchCounter.getCoveredCount,
        "branch-rate" -> bundle.getBranchCounter.getCoveredRatio,
        "complexity" -> bundle.getComplexityCounter.getCoveredRatio,
        "version" -> "1.0",
        "timestamp" -> System.currentTimeMillis()
      ),
      () => {
        writeTag(
          "sources",
          Map.empty,
          () => {
            val sourcePathsRoot: Seq[File] = locator.asInstanceOf[DirectoriesSourceFileLocator].directories
            val hashSet = new mutable.HashSet[File]()
            sourcePathsRoot.foreach { p =>
              val arr: Array[File] = p.listFiles((dir, name) => name != "resources")
              if (arr != null) arr.foreach(hashSet.add)
            }
            val sourcePaths = hashSet.toList
            writer.write("        <source>--source</source>\n")
            sourcePaths.foreach(source => writer.write(s"        <source>${source.getPath}</source>\n"))
          }
        )
        writePackages(bundle.getPackages)
      }
    )
  }

  override def visitGroup(name: String): IReportGroupVisitor = groupVisitor

}

class CoberturaFormatter() {
  def createVisitor(output: OutputStream): IReportVisitor = {
    new CoberturaRootVisitor(new OutputStreamWriter(output, "UTF-8"))
  }
}
