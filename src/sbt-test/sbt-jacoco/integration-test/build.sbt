libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.12.21"

enablePlugins(JacocoItPlugin)
