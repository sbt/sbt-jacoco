import sbt._
import Keys._
import de.johoop.jacoco4sbt._
import JacocoPlugin._

object TopLevel extends Build
{
    lazy val jacocoTest = Project(
        id = "jacocoTest",
        base = file( "." ),
        settings = Defaults.defaultSettings ++ jacoco.settings ++ Seq(
            libraryDependencies ++= Seq("org.scalatest" %% "scalatest" % "2.2.0" % Test),
            scalaVersion := "2.10.5",
            scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_"),
            organization := "com.navetas",
            fork in jacoco.Config := false,
            jacoco.thresholds in jacoco.Config := Thresholds(instruction = 100, method = 100, branch = 100, complexity = 100, line = 100, clazz = 100)
        )
    )
}
