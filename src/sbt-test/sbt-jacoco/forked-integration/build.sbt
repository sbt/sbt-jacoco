libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.13.7"

enablePlugins(JacocoItPlugin)

Test / fork := true
IntegrationTest / fork := true
