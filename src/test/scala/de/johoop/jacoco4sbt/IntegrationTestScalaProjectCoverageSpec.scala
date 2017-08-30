package de.johoop.jacoco4sbt

import org.specs2._
import de.johoop.jacoco4sbt.build.BuildInfo
import scala.sys.process.Process
import java.io.File
import org.specs2.matcher.FileMatchers

class IntegrationTestScalaProjectCoverageSpec extends Specification with FileMatchers { def is = args(sequential = true) ^ s2"""
  JaCoCo in an integration test Scala project

  Covering tests in a simple Scala project should
    return an exit code == 0 when required coverage is met,                  $e1
    create a jacoco target directory,                                        $e2
    create a classes directory.                                              $e3
    create an it-jacoco target directory,                                    $e4
    create a it classes directory.                                           $e5
"""

  lazy val testProjectBase = new File(BuildInfo.test_resourceDirectory, "jacocoIntegrationTest")
  lazy val targetDir = new File(testProjectBase, "target")
  lazy val jacocoDir = new File(targetDir, "scala-2.10/jacoco")
  lazy val itJacocoDir = new File(targetDir, "scala-2.10/it-jacoco")
  lazy val coveredClassesDir = new File(jacocoDir, "classes")
  lazy val coveredItClassesDir = new File(itJacocoDir, "classes")

  lazy val exitCode = Process(s"${Util.processName} -Dsbt.version=${BuildInfo.sbtVersion} -Dplugin.version=${BuildInfo.version} clean jacoco:cover it-jacoco:cover", Some(testProjectBase)) !

  def e1 = exitCode should be equalTo(0)
  def e2 = jacocoDir should exist and beADirectory
  def e3 = coveredClassesDir should exist and beADirectory
  def e4 = itJacocoDir should exist and beADirectory
  def e5 = coveredItClassesDir should exist and beADirectory
}
