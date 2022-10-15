name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.17"
scalacOptions ++= Seq("-deprecation", "-optimize", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.14" % "test"

Compile / unmanagedResourceDirectories += sourceDirectory.value / "unmanaged"
