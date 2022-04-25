libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.12" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.12.15"

enablePlugins(JacocoItPlugin)
