package de.johoop.jacoco4sbt

import sbt._
import complete.Parser
import complete.DefaultParsers._ 

trait CommandGrammar {
  lazy val grammar = Space ~> (Instrument | Uninstrument | Clean | reportGrammar) 
  lazy val reportGrammar = Report ~ (Space ~> reportFormat).*
  lazy val reportFormat = (Html | Xml | Csv ) ~ Encoding.?
  
  lazy val Clean : Parser[String] = "clean"
  lazy val Instrument : Parser[String] = "instrument"
  lazy val Uninstrument : Parser[String] = "uninstrument"
  lazy val Report : Parser[String] = "report"
    
  lazy val Html : Parser[String] = "html"
  lazy val Xml : Parser[String]  = "xml"
  lazy val Csv : Parser[String]  = "csv"
    
  lazy val Encoding : Parser[Seq[Char]] = '(' ~> charClass( _ != ')', "encoding").+ <~ ')'
}