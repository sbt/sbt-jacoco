package de.johoop.jacoco4sbt

import org.specs2._
import de.johoop.jacoco4sbt.build.BuildInfo
import scala.sys.process.Process
import java.io.File
import org.specs2.matcher.FileMatchers

class SimpleScalaProjectCoverageSpec extends Specification with FileMatchers { def is = args(sequential = true) ^ s2"""
  JaCoCo in a simple Scala project

  Covering tests in a simple Scala project should
    return an exit code != 0 when required coverage is not met,    $e1
    create a jacoco target directory,                                        $e2
    create a classes directory.                                              $e3
"""

  lazy val testProjectBase = new File(BuildInfo.test_resourceDirectory, "jacocoTest")
  lazy val targetDir = new File(testProjectBase, "target")
  lazy val jacocoDir = new File(targetDir, "scala-2.10/jacoco")
  lazy val coveredClassesDir = new File(jacocoDir, "classes")

  lazy val exitCode = Process(s"${Util.processName} -Dsbt.version=${BuildInfo.sbtVersion} -Dplugin.version=${BuildInfo.version} clean jacoco:cover", Some(testProjectBase)) !

  def e1 = exitCode should not be equalTo(0)
  def e2 = jacocoDir should exist and beADirectory
  def e3 = coveredClassesDir should exist and beADirectory
}
