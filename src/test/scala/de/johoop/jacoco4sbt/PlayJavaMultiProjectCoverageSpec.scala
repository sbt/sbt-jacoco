package de.johoop.jacoco4sbt

import org.specs2._
import de.johoop.jacoco4sbt.build.BuildInfo
import scala.sys.process.Process
import java.io.File
import org.specs2.matcher.FileMatchers

class PlayJavaMultiProjectCoverageSpec extends Specification with FileMatchers { def is = s2"""
  $sequential
  ${"JaCoCo in a Play Java multi-project".title}

  Covering tests in a Play Scala project with subprojects should
    return an exit code of zero,                                  $e1
    create a jacoco target directory in each subproject,          $e2
    create a classes directory in each subproject.                $e3
"""

  lazy val testProjectBase = new File(BuildInfo.test_resourceDirectory, "play-multi-project-jacoco")
  lazy val targets = List("target", "modules/common/target")
  lazy val targetDirs = targets map (new File(testProjectBase, _))
  lazy val jacocoDirs = targetDirs map (new File(_, "scala-2.10/jacoco"))
  lazy val coveredClassesDirs = jacocoDirs map (new File(_, "classes"))

  lazy val exitCode = Process("sbt.bat clean jacoco:cover", Some(testProjectBase)) !

  def e1 = exitCode should be equalTo(0)
  def e2 = jacocoDirs must contain(beADirectory).foreach
  def e3 = coveredClassesDirs must contain(beADirectory).foreach
}
