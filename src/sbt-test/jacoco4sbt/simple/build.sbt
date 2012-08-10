import de.johoop.jacoco4sbt._
import JacocoPlugin._

version := "0.0.1"

seq(jacoco.settings: _*)
