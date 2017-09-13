name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.3"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.4" % "test"

fork in Test := true