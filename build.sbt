name := "sbt-jacoco"
organization := "org.scala-sbt"

version := "3.0.0-M5-SNAPSHOT"

sbtPlugin := true
crossSbtVersions := Seq("0.13.16", "1.0.1")

val jacocoVersion = "0.7.9"

libraryDependencies ++= Seq(
  "org.jacoco"    %  "org.jacoco.core"      % jacocoVersion,
  "org.jacoco"    %  "org.jacoco.report"    % jacocoVersion,
  "com.jsuereth"  %% "scala-arm"            % "2.0",
  "org.scalatest" %% "scalatest"            % "3.0.4"         % Test,
  "org.mockito"   %  "mockito-all"          % "1.10.19"       % Test
)

scalacOptions ++= Seq(
  "-unchecked",
  "-deprecation",
  "-feature",
  "-Xfuture",
  "-Ywarn-adapted-args",
  "-Ywarn-dead-code")

enablePlugins(BuildInfoPlugin)
buildInfoPackage := "org.scalasbt.jacoco.build"
buildInfoKeys := Seq[BuildInfoKey](
  resourceDirectory in Test,
  version,
  "jacocoVersion" -> jacocoVersion,
  "sbtVersion" -> (sbtVersion in pluginCrossBuild).value
)

licenses += (("Eclipse Public License v1.0", url("http://www.eclipse.org/legal/epl-v10.html")))
headerLicense := Some(HeaderLicense.Custom(
  """|This file is part of sbt-jacoco.
     |
     |Copyright (c) Joachim Hofer & contributors
     |All rights reserved.
     |
     |This program and the accompanying materials
     |are made available under the terms of the Eclipse Public License v1.0
     |which accompanies this distribution, and is available at
     |http://www.eclipse.org/legal/epl-v10.html
     |""".stripMargin
))
