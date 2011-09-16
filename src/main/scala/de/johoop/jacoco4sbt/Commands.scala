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

        loadExecutionData("some-file") // TODO
        
        println("generating html report... (TODO)")
        
        
        buildState
      }
    }
  }

  def loadExecutionData(filename: String) = {
    import org.jacoco.core.data._
    
    val executionDataStore = new ExecutionDataStore
    val sessionInfoStore = new SessionInfoStore
    val fis = new FileInputStream(filename)
    try {
      val executionDataReader = new ExecutionDataReader(fis)
      
  
      executionDataReader setExecutionDataVisitor executionDataStore
      executionDataReader setSessionInfoVisitor sessionInfoStore
  
      while (executionDataReader.read()) {}
        
    } finally {
      fis.close()
    }
    
    (executionDataStore, sessionInfoStore)
  }
  
  def extractedState(implicit state: State) = Project extract state
  def extractedSettings(implicit state: State) = extractedState.structure.data
  def addSettings(settings: Seq[Project.Setting[_]])(implicit state: State) = extractedState.append(settings, state)

}