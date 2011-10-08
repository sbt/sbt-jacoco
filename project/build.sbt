resolvers += Resolver.url("Typesafe repository - Snapshots", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-snapshots/"))(Patterns(false, "[organisation]/[module]/[revision]/jars/[artifact].[ext]"))

resolvers += Resolver.url("Typesafe repository - Releases", new java.net.URL("http://typesafe.artifactoryonline.com/typesafe/ivy-releases/"))(Patterns(false, "[organisation]/[module]/[revision]/jars/[artifact].[ext]"))

libraryDependencies <++= (sbtVersion) { sv => Seq(
  "org.scala-tools.sbt" %% "scripted-plugin" % sv,
  "org.scala-tools.sbt" %% "scripted-sbt" % sv) }
