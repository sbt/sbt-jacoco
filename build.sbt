lazy val jacocoVersion = "0.8.8"

ThisBuild / version := {
  if ((ThisBuild / isSnapshot).value) "3.4.0" + "-SNAPSHOT"
  else (ThisBuild / version).value
}

lazy val jacocoPlugin = (project in file("."))
  .enablePlugins(SbtPlugin)
  .enablePlugins(BuildInfoPlugin)
  .enablePlugins(ParadoxSitePlugin)
  .enablePlugins(GhpagesPlugin)
  .settings(nocomma {
    name := "sbt-jacoco"

    libraryDependencies ++= Seq(
      "org.jacoco" % "org.jacoco.core" % jacocoVersion,
      "org.jacoco" % "org.jacoco.report" % jacocoVersion,
      "com.jsuereth" %% "scala-arm" % "2.0",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.14.1",
      "org.scalaj" %% "scalaj-http" % "2.4.2",
      "commons-codec" % "commons-codec" % "1.15",
      "org.eclipse.jgit" % "org.eclipse.jgit" % "5.13.1.202206130422-r",
      "org.scalatest" %% "scalatest" % "3.2.14" % Test,
      "org.mockito" % "mockito-core" % "4.8.1" % Test
    )

    scalacOptions ++= Seq(
      "-unchecked",
      "-deprecation",
      "-feature",
      "-Xfuture",
      "-Ywarn-adapted-args",
      "-Ywarn-dead-code"
    )

    buildInfoPackage := "com.github.sbt.jacoco.build"
    buildInfoKeys := Seq[BuildInfoKey](
      Test / resourceDirectory,
      version,
      "jacocoVersion" -> jacocoVersion,
      "sbtVersion" -> sbtVersion.value
    )

    Compile / paradoxNavigationDepth := 3
    git.remoteRepo := "git@github.com:sbt/sbt-jacoco.git"

    headerLicense := Some(
      HeaderLicense.Custom(
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
      )
    )

    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    }

    scriptedBufferLog := false
  })

Global / onChangedBuildSource := ReloadOnSourceChanges
ThisBuild / organization := "com.github.sbt"
ThisBuild / description := "an sbt plugin for JaCoCo Code Coverage"
ThisBuild / homepage := Some(url("https://www.scala-sbt.org/sbt-jacoco/"))
ThisBuild / licenses += (("Eclipse Public License v1.0", url("http://www.eclipse.org/legal/epl-v10.html")))
ThisBuild / developers := List(
  Developer(
    "jmhofer",
    "Joachim Hofer",
    "@jmhofer",
    url("https://github.com/jmhofer")
  )
)
ThisBuild / pomIncludeRepository := { _ =>
  false
}
ThisBuild / publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value) Some("snapshots" at nexus + "content/repositories/snapshots")
  else Some("releases" at nexus + "service/local/staging/deploy/maven2")
}
ThisBuild / publishMavenStyle := true
ThisBuild / dynverSonatypeSnapshots := true
