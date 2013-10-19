publishTo := { 
  val qualifier = "sbt-plugin-" + (if (version.value contains "-SNAPSHOT") "snapshots" else "releases")
  Some(Resolver.url(qualifier, new URL(s"http://repo.scala-sbt.org/scalasbt/$qualifier"))(Resolver.ivyStylePatterns))
}

publishMavenStyle := false

publishArtifact in Test := false
