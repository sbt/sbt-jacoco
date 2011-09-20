sbtPlugin := true

publishMavenStyle := false

name := "jacoco4sbt"

organization := "de.johoop"

version := "1.0.2"

publishTo := Some("Scala Tools Nexus" at "http://nexus.scala-tools.org/content/repositories/releases/")

credentials += Credentials(Path.userHome / ".ivy2" / ".credentials")

ivyXML := 
  <dependencies>
    <dependency org="org.jacoco" name="org.jacoco.core" rev="0.5.3.201107060350" >
        <artifact name="org.jacoco.core" type="jar" />
    </dependency>
    <dependency org="org.jacoco" name="org.jacoco.report" rev="0.5.3.201107060350" >
        <artifact name="org.jacoco.report" type="jar" />
    </dependency>
  </dependencies>
   
scalacOptions ++= Seq("-unchecked", "-deprecation")

//seq(ScriptedPlugin.scriptedSettings: _*)
