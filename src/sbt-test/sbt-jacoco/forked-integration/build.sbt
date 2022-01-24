libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.11" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.12.15"

enablePlugins(JacocoItPlugin)

Test / fork := true
IntegrationTest / fork := true
