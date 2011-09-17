package de.johoop.jacoco4sbt

import sbt._
import Keys._
import complete.Parsers._
import java.io.FileInputStream

trait Commands extends Keys {
  private lazy val Instrument = "instrument"
  private lazy val Uninstrument = "uninstrument"
  private lazy val Report = "report"

  private lazy val grammar = Space ~> Instrument | Space ~> Uninstrument | Space ~> Report

  private[jacoco4sbt] lazy val jacocoCommand = Command("jacoco")(_ => grammar) { (buildState, arguments) =>

    implicit val implicitState = buildState

    arguments match {
      case Instrument => {
        println("instrumenting...")
        val agentFile = extractedState.evalTask(unpackAgent, buildState)
        val agentJavaOption = "-javaagent:%s=output=file,destfile=./jacoco.exec" format agentFile.getAbsolutePath

        addSettings(Seq(
            javaOptions in run += agentJavaOption))
      }

      case Uninstrument => {
        println("uninstrumenting...")

        addSettings(Seq(
            javaOptions in run <<= (javaOptions) { _ filter (_.contains("-javaagent:")) } ))
      }

      case Report => {
        import org.jacoco.core.analysis._
        import org.jacoco.core.data._
        import org.jacoco.report._

        // TODO different report kinds
        // TODO retrieve and fill in all the parameters
        val report = new Report(
            executionDataFile = new File("jacoco.exec"),
            classesDirectory = new File("bla"),
            sourceDirectory = new File("src"),
            reportDirectory = new File("target/jacoco/report"),
            title = "blabla")
        report.generate

        println("generating html report... (TODO)")


        buildState
      }
    }
  }

  def extractedState(implicit state: State) = Project extract state
  def extractedSettings(implicit state: State) = extractedState.structure.data
  def addSettings(settings: Seq[Project.Setting[_]])(implicit state: State) = extractedState.append(settings, state)

}