publishTo := {
  val r = if (isSnapshot.value) Resolver.sbtPluginRepo("snapshots")
          else Resolver.sbtPluginRepo("releases")
  Some(r)
}

publishMavenStyle := false

publishArtifact in Test := false
