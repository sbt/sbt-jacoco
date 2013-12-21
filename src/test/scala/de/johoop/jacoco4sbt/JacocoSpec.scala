package de.johoop.jacoco4sbt

import org.specs2._
import de.johoop.jacoco4sbt.build.BuildInfo
import scala.sys.process.Process
import java.io.File
import org.specs2.matcher.FileMatchers

class JacocoSpec extends Specification with FileMatchers { def is = s2"""
  $sequential
  ${"JaCoCo".title}

  Covering tests should
    return an exit code of zero,                             $e1
    create a jacoco target directory,                        $e2
    create a classes directory.                              $e3
"""

  lazy val testProjectBase = new File(BuildInfo.test_resourceDirectory, "jacocoTest")
  lazy val targetDir = new File(testProjectBase, "target")
  lazy val jacocoDir = new File(targetDir, "scala-2.10/jacoco")
  lazy val coveredClassesDir = new File(jacocoDir, "classes")

  // side effect, maybe do this in a Specs2 "step" instead
  lazy val exitCode = Process("sbt.bat clean jacoco:cover", Some(testProjectBase)) !

  def e1 = exitCode should be equalTo(0)
  def e2 = jacocoDir should beADirectory
  def e3 = coveredClassesDir should beADirectory
}
