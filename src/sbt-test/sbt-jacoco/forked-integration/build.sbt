libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.12.16"

enablePlugins(JacocoItPlugin)

Test / fork := true
IntegrationTest / fork := true
