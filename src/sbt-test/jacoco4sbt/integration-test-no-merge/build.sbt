libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.0" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.10.4"

jacoco.settings

Defaults.itSettings

itJacoco.settings

(itJacoco.mergeReports in itJacoco.Config) := false
//merge in itJacoco.Config := false

//itJacoco.mergeReports := false