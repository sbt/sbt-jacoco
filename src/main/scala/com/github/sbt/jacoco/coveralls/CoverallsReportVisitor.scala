package com.github.sbt.jacoco.coveralls

import java.io.File
import java.{util => ju}

import com.fasterxml.jackson.core.{JsonEncoding, JsonFactory}
import org.apache.commons.codec.digest.DigestUtils
import org.jacoco.core.analysis.{IBundleCoverage, ILine, IPackageCoverage, ISourceFileCoverage}
import org.jacoco.core.data.{ExecutionData, SessionInfo}
import org.jacoco.report.{IReportGroupVisitor, IReportVisitor, ISourceFileLocator}
import sbt._

import scala.collection.JavaConverters._

class CoverallsReportVisitor(
    output: File,
    sourceDirs: Seq[File],
    projectRootDir: File,
    jobId: String,
    repoToken: Option[String])
    extends IReportVisitor
    with IReportGroupVisitor {

  private val digest = new DigestUtils("MD5")

  private val jsonFactory = new JsonFactory()
  private val json = jsonFactory.createGenerator(output, JsonEncoding.UTF8)

  json.writeStartObject()

  repoToken foreach { token =>
    json.writeStringField("repo_token", token)
  }

  json.writeStringField("service_job_id", jobId)
  json.writeStringField("service_name", "travis-ci")

  json.writeArrayFieldStart("source_files")

  override def visitInfo(sessionInfos: ju.List[SessionInfo], executionData: ju.Collection[ExecutionData]): Unit = {}

  override def visitGroup(name: String): IReportGroupVisitor = this

  override def visitBundle(bundle: IBundleCoverage, locator: ISourceFileLocator): Unit = {
    bundle.getPackages.asScala foreach { pkg: IPackageCoverage =>
      pkg.getSourceFiles.asScala foreach { source: ISourceFileCoverage =>
        json.writeStartObject()

        //noinspection ScalaStyle
        val (filename, md5) = findFile(pkg.getName, source.getName) match {
          case Some(file) =>
            (IO.relativize(projectRootDir, file).getOrElse(file.getName), digest.digestAsHex(file))

          case None =>
            (source.getName, "")
        }

        json.writeStringField("name", filename)
        json.writeStringField("source_digest", md5)

        json.writeArrayFieldStart("coverage")

        (0 to source.getLastLine) foreach { l =>
          val line: ILine = source.getLine(l)

          if (line.getInstructionCounter.getTotalCount == 0) {
            // non-code line
            json.writeNull()
          } else {
            json.writeNumber(line.getInstructionCounter.getCoveredCount)
          }
        }

        json.writeEndArray()

        json.writeEndObject()
      }
    }
  }

  override def visitEnd(): Unit = {
    json.writeEndArray()
    json.writeEndObject()
    json.close()
  }

  private def findFile(packageName: String, fileName: String): Option[File] = {
    // TODO make common with source file locator
    sourceDirs.map(d => d / packageName / fileName).find(_.exists())
  }
}
