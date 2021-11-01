name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.13.7"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.10" % "test"

Compile / unmanagedResourceDirectories += sourceDirectory.value / "unmanaged"
