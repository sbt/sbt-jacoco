package de.johoop.jacoco4sbt

import sbt._
import Keys._
import complete.Parsers._
import CommandSupport.logger

trait Commands extends Keys {
  private lazy val Instrument = "instrument"
  private lazy val Uninstrument = "uninstrument"
  private lazy val Report = "report"

  private lazy val grammar = Space ~> Instrument | Space ~> Uninstrument | Space ~> Report

  private[jacoco4sbt] lazy val jacocoCommand = Command("jacoco")(_ => grammar) { (buildState, arguments) =>

    implicit val implicitState = buildState

    arguments match {
      case Instrument => instrument 
      case Uninstrument => uninstrument
      case Report => report
    }
  }

  def instrument(implicit buildState: State) = {
    logger(buildState).info("Instrumenting the run tasks.")

    val agentFile = extractedState.evalTask(unpackAgent, buildState)
    val agentJavaOption = "-javaagent:%s=output=file,destfile=./jacoco.exec" format agentFile.getAbsolutePath

    addSettings(Seq(
        javaOptions in run += agentJavaOption))
    
  }

  def uninstrument(implicit buildState: State) = {
    logger(buildState).info("Uninstrumenting the run tasks.")

    addSettings(Seq(javaOptions in run <<= (javaOptions) { _ filter (_.contains("-javaagent:")) } ))
  }

  def report(implicit buildState: State) = {
    logger(buildState).info("Generating JaCoCo coverage report.")

    Project.evaluateTask(jacocoReport in Config, buildState)

    buildState
  }
  
  def extractedState(implicit state: State) = Project extract state
  def extractedSettings(implicit state: State) = extractedState.structure.data
  def addSettings(settings: Seq[Project.Setting[_]])(implicit state: State) = extractedState.append(settings, state)

}