package de.johoop.jacoco4sbt

import org.jacoco.report.DirectorySourceFileLocator
import org.jacoco.report.ISourceFileLocator

import java.io.File

class DirectoriesSourceFileLocator(directories: Seq[File], sourceEncoding: String, tabWidth: Int) 
    extends ISourceFileLocator {
  
  override def getSourceFile(packageName: String, fileName: String) = {
    def findInDirectory(dir: File) = Option(dirSourceLocator(dir).getSourceFile(packageName, fileName))
    def dirSourceLocator(dir: File) = new DirectorySourceFileLocator(dir, sourceEncoding, tabWidth)
    
    (directories flatMap findInDirectory).headOption getOrElse null
  }
  
  override def getTabWidth = tabWidth
}