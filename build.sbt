name := "sbt-jacoco"
organization := "com.github.sbt"

version in ThisBuild := "3.0.3-SNAPSHOT"

sbtPlugin := true
crossSbtVersions := Seq("0.13.16", "1.0.2")

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
buildInfoPackage := "com.github.sbt.jacoco.build"
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

lazy val docs = (project in file("docs"))
  .enablePlugins(ParadoxPlugin)
.settings(
  name := "Hello Project",
    paradoxNavigationDepth := 3
)
