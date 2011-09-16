package de.johoop.jacoco4sbt

import sbt._
import Keys._
import complete.Parsers._

object JacocoPlugin extends Plugin {

  lazy val jacocoConfig = config("jacoco") hide

  val jacocoDependencies = Seq(
    "org.jacoco" % "org.jacoco.agent" % "0.5.3.201107060350" % "jacoco" artifacts(Artifact("org.jacoco.agent", "jar", "jar")),
    "org.jacoco" % "org.jacoco.core" % "0.5.3.201107060350" % "jacoco" artifacts(Artifact("org.jacoco.core", "jar", "jar")),
    "org.jacoco" % "org.jacoco.report" % "0.5.3.201107060350" % "jacoco" artifacts(Artifact("org.jacoco.report", "jar", "jar")))

  lazy val unpackJacocoAgent = TaskKey[File]("unpack-jacoco-agent", "Unpacks the Jacoco Agent JAR")

  def unpackJacocoAgentAction(libManagedJacoco: File, classpath: Classpath) = {
    val outerAgentJar = classpath.files find (_.getName contains "agent")
    IO.unzip(outerAgentJar.get, libManagedJacoco, "*.jar").head
  }

  lazy val jacocoClasspath = TaskKey[Classpath]("jacoco-classpath")

  override val settings = Seq(
    commands += cmd,
    ivyConfigurations += jacocoConfig,
    libraryDependencies ++= jacocoDependencies,
    jacocoClasspath <<= (classpathTypes, update) map { Classpaths managedJars (jacocoConfig, _, _) },
    unpackJacocoAgent <<= (managedDirectory in jacocoConfig, jacocoClasspath) map unpackJacocoAgentAction)

  private lazy val Instrument = "instrument"
  private lazy val Uninstrument = "uninstrument"

  private lazy val grammar = Space ~> Instrument | Space ~> Uninstrument

  private lazy val cmd = Command("jacoco")(_ => grammar) { (state, arguments) =>
    import scalaz.Scalaz.{state => scalazState, _}

    implicit val implicitState = state

    if (arguments === Instrument) {
      println("instrumenting...")
      val agentFile = extractedState.evalTask(unpackJacocoAgent, state)
      val agentJavaOption = "-javaagent:%s=output=file,destfile=./jacoco.exec" format agentFile.getAbsolutePath

      addSettings(Seq(
          javaOptions in run += agentJavaOption))

    } else {
      println("uninstrumenting... (todo)")
      state
    }
  }

  def extractedState(implicit state: State) = Project extract state
  def extractedSettings(implicit state: State) = extractedState.structure.data
  def addSettings(settings: Seq[Project.Setting[_]])(implicit state: State) = extractedState.append(settings, state)
}
