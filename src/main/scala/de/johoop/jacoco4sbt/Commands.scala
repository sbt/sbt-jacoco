package de.johoop.jacoco4sbt

import sbt._
import Keys._
import complete.Parsers._

trait Commands extends Keys {
  private lazy val Instrument = "instrument"
  private lazy val Uninstrument = "uninstrument"

  private lazy val grammar = Space ~> Instrument | Space ~> Uninstrument

  private[jacoco4sbt] lazy val jacocoCommand = Command("jacoco")(_ => grammar) { (state, arguments) =>
    import scalaz.Scalaz.{state => scalazState, _}

    implicit val implicitState = state

    if (arguments === Instrument) {
      println("instrumenting...")
      val agentFile = extractedState.evalTask(unpackAgent, state)
      val agentJavaOption = "-javaagent:%s=output=file,destfile=./jacoco.exec" format agentFile.getAbsolutePath

      addSettings(Seq(
          javaOptions in run += agentJavaOption))

    } else {
      println("uninstrumenting...")
      
      addSettings(Seq(
          javaOptions in run <<= (javaOptions) { _ filter (_.contains("-javaagent:")) } ))
    }
  }

  def extractedState(implicit state: State) = Project extract state
  def extractedSettings(implicit state: State) = extractedState.structure.data
  def addSettings(settings: Seq[Project.Setting[_]])(implicit state: State) = extractedState.append(settings, state)

}