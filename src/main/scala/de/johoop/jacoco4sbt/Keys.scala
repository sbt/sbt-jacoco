package de.johoop.jacoco4sbt

import sbt._
import Keys._

trait Keys {
  lazy val Config = config("jacoco") hide
  
  lazy val jacocoClasspath = TaskKey[Classpath]("jacoco-classpath")
  
  lazy val unpackAgent = TaskKey[File]("unpack-jacoco-agent", "Unpacks the Jacoco Agent JAR")
}
