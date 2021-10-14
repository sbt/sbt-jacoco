libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.12.15"

enablePlugins(JacocoItPlugin)

Test / fork := true
IntegrationTest / fork := true