name := "jacoco4sbt"
organization := "de.johoop"

version := "3.0.0-SNAPSHOT"

sbtPlugin := true
crossSbtVersions := Seq("0.13.16", "1.0.1")

val jacocoVersion = "0.7.9"

libraryDependencies ++= Seq(
  "org.jacoco"  %  "org.jacoco.core"      % jacocoVersion,
  "org.jacoco"  %  "org.jacoco.report"    % jacocoVersion,
  "org.specs2"  %% "specs2-core"          % "3.9.1"         % Test,
  "org.specs2"  %% "specs2-matcher-extra" % "3.9.1"         % Test,
  "org.mockito" %  "mockito-all"          % "1.10.19"       % Test,
  "org.pegdown" %  "pegdown"              % "1.6.0"         % Test
)

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:_")
scalacOptions in Test ++= Seq("-Yrangepos")

enablePlugins(BuildInfoPlugin)
buildInfoPackage := "de.johoop.jacoco4sbt.build"
buildInfoKeys := Seq[BuildInfoKey](
  resourceDirectory in Test,
  version,
  "jacocoVersion" -> jacocoVersion,
  "sbtVersion" -> (sbtVersion in pluginCrossBuild).value
)

headerLicense := Some(HeaderLicense.Custom(
  """|This file is part of jacoco4sbt.
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
