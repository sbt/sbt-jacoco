/*
 * This file is part of jacoco4sbt.
 * 
 * Copyright (c) 2011 Joachim Hofer
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package de.johoop.jacoco4sbt

import sbt._
import complete.Parser
import complete.DefaultParsers._ 

trait CommandGrammar {
  type ReportFormatResult = List[Tuple2[String, Option[Seq[Char]]]]
  
  lazy val Grammar = Space ~> (Instrument | Uninstrument | Clean | ReportGrammar) 
  lazy val ReportGrammar = Report ~> (Space ~> ReportFormat).*
  lazy val ReportFormat = (Html | Xml | Csv ) ~ Encoding.?
  
  lazy val Clean = token("clean")
  lazy val Instrument = token("instrument")
  lazy val Uninstrument = token("uninstrument")
  lazy val Report = token("report")
    
  lazy val Html = token("html")
  lazy val Xml = token("xml")
  lazy val Csv = token("csv")
    
  lazy val Encoding : Parser[Seq[Char]] = '(' ~> charClass( _ != ')', "encoding").+ <~ ')'
}