package de.johoop.jacoco4sbt

case class Thresholds(instruction: Double = 0,
                      method: Double = 0,
                      branch: Double = 0,
                      complexity: Double = 0,
                      line: Double = 0,
                      clazz: Double = 0)
