name := "sbt-jacoco"
organization := "com.github.sbt"

version in ThisBuild := "3.1.0-SNAPSHOT"

sbtPlugin := true
crossSbtVersions := Seq("0.13.17", "1.1.6")

val jacocoVersion = "0.7.9"
val circeVersion = "0.8.0"

libraryDependencies ++= Seq(
  "org.jacoco"                  %  "org.jacoco.core"      % jacocoVersion,
  "org.jacoco"                  %  "org.jacoco.report"    % jacocoVersion,
  "com.jsuereth"                %% "scala-arm"            % "2.0",
  "com.fasterxml.jackson.core"  %  "jackson-core"         % "2.9.6",
  "org.scalaj"                  %% "scalaj-http"          % "2.4.0",
  "commons-codec"               %  "commons-codec"        % "1.11",
  "org.eclipse.jgit"            %  "org.eclipse.jgit"     % "4.11.0.201803080745-r",
  "org.scalatest"               %% "scalatest"            % "3.0.5"         % Test,
  "org.mockito"                 %  "mockito-all"          % "1.10.19"       % Test
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

enablePlugins(ParadoxSitePlugin, GhpagesPlugin)
paradoxNavigationDepth in Paradox  := 3
git.remoteRepo := "git@github.com:sbt/sbt-jacoco.git"
