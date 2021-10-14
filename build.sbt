lazy val jacocoVersion = "0.8.7"
lazy val circeVersion = "0.8.0"

ThisBuild / organization := "com.github.sbt"
ThisBuild / version := {
  if ((ThisBuild / isSnapshot).value) (ThisBuild / version).value + "-SNAPSHOT"
  else (ThisBuild / version).value
}
ThisBuild / scalaVersion := "2.12.14"
ThisBuild / licenses += (("Eclipse Public License v1.0", url("http://www.eclipse.org/legal/epl-v10.html")))


lazy val jacocoPlugin = (project in file("."))
  .enablePlugins(SbtPlugin)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(nocomma {
    name := "sbt-jacoco"

    libraryDependencies ++= Seq(
      "org.jacoco"                  %  "org.jacoco.core"      % jacocoVersion,
      "org.jacoco"                  %  "org.jacoco.report"    % jacocoVersion,
      "com.jsuereth"                %% "scala-arm"            % "2.0",
      "com.fasterxml.jackson.core"  %  "jackson-core"         % "2.11.3",
      "org.scalaj"                  %% "scalaj-http"          % "2.4.2",
      "commons-codec"               %  "commons-codec"        % "1.15",
      "org.eclipse.jgit"            %  "org.eclipse.jgit"     % "5.9.0.202009080501-r",
      "org.scalatest"               %% "scalatest"            % "3.0.5"         % Test,
      "org.mockito"                 %  "mockito-core"          % "3.12.4"         % Test
    )

    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xfuture",
      "-Ywarn-adapted-args",
      "-Ywarn-dead-code")

    buildInfoPackage := "com.github.sbt.jacoco.build"
    buildInfoKeys := Seq[BuildInfoKey](
      Test / resourceDirectory,
      version,
      "jacocoVersion" -> jacocoVersion,
      "sbtVersion" -> sbtVersion.value
    )

    Paradox / paradoxNavigationDepth := 3
    git.remoteRepo := "git@github.com:sbt/sbt-jacoco.git"

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
  })
