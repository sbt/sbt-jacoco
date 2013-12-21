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
            libraryDependencies ++= Seq( "org.scalatest" %% "scalatest" % "1.9.1" % "test" ),
            scalaVersion := "2.10.3",
            scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_"),
            organization := "com.navetas"
        )
    )
}
