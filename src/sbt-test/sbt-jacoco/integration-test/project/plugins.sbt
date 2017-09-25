{
  val pluginVersion = System.getProperty("plugin.version")
  if (pluginVersion == null)
    throw new RuntimeException("""|The system property 'plugin.version' is not defined.
                                  |Specify this property using the parameter -Dplugin.version=<version>""".stripMargin)

  else addSbtPlugin("com.github.sbt" % "sbt-jacoco" % pluginVersion)
}
