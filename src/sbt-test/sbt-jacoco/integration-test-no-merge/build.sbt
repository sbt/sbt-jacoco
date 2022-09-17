libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.13" % "test,it"

val root = project.in(file(".")).configs(IntegrationTest)
scalaVersion := "2.12.17"

enablePlugins(JacocoItPlugin)

IntegrationTest / jacocoAutoMerge := false
