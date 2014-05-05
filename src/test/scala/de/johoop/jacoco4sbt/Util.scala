package de.johoop.jacoco4sbt

object Util {
  def processName = if (System.getProperty("os.name").contains("Windows")) "sbt.bat" else "sbt"
}
