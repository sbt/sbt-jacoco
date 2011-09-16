package de.johoop.jacoco4sbt

import sbt._

trait Keys {
  lazy val unpackAgent = TaskKey[File]("unpack-jacoco-agent", "Unpacks the Jacoco Agent JAR")
}
