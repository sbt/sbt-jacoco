libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.20" % "test,it"

val root = project.in(file(".")).configs(JacocoIntegrationTest)
scalaVersion := "2.12.21"

enablePlugins(JacocoItPlugin)

Test / fork := true
JacocoIntegrationTest / fork := true
