bintrayOrganization := None
bintrayRepository := "sbt-plugins"
bintrayPackage <<= name
publishArtifact in Test := false
publishMavenStyle := false
