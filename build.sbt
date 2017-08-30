lazy val root = (project in file(".")).enablePlugins(BuildInfoPlugin).settings(

  name := "jacoco4sbt",
  organization := "de.johoop",
  version := "2.4.0-SNAPSHOT",
  scalaVersion := "2.10.6",

  crossSbtVersions := Vector("0.13.16", "1.0.0"),

  sbtPlugin := true,

  libraryDependencies ++= Seq(
    "org.jacoco"  %  "org.jacoco.core"      % jacocoVersion artifacts jacocoCore,
    "org.jacoco"  %  "org.jacoco.report"    % jacocoVersion artifacts jacocoReport,
    "org.specs2"  %% "specs2-core"          % "3.9.1" % Test,
    "org.specs2"  %% "specs2-matcher-extra" % "3.9.1" % Test,

    "org.mockito" %  "mockito-all"          % "1.10.19"  % Test,
    "org.pegdown" %  "pegdown"              % "1.6.0"  % Test
  ),

  scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:_"),

  buildInfoKeys := Seq[BuildInfoKey](
    resourceDirectory in Test,
    version,
    "jacocoVersion" -> jacocoVersion,
    "sbtVersion" -> (sbtVersion in pluginCrossBuild).value
  ),
  buildInfoPackage := "de.johoop.jacoco4sbt.build",

  test in Test := (test in Test dependsOn publishLocal).value,
  parallelExecution in Test := false,
  scalacOptions in Test ++= Seq("-Yrangepos")
)

lazy val jacocoCore    = Artifact("org.jacoco.core", "jar", "jar")
lazy val jacocoReport  = Artifact("org.jacoco.report", "jar", "jar")
lazy val jacocoVersion = "0.7.9"
