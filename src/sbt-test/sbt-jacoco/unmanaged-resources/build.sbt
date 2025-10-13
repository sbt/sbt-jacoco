name := "jacocoTest"
organization := "com.navetas"

scalaVersion := "2.12.20"
scalacOptions ++= Seq("-deprecation", "-unchecked", "-Xlint", "-language:_")

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.19" % "test"

Compile / unmanagedResourceDirectories += sourceDirectory.value / "unmanaged"
