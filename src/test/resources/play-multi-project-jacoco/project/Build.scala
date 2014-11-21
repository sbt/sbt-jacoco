import sbt._
import play.Project._
import Keys._
import de.johoop.jacoco4sbt.JacocoPlugin._


object ApplicationBuild extends Build {

  val appVersion = "1.0-SNAPSHOT"

    
  // So we can get code coverage
  lazy val jacocoSettings = Seq(
      parallelExecution in jacoco.Config := false,
      jacoco.excludes in jacoco.Config := Seq("Routes*","*Reverse*","*anonfun*", "*routes*"),
      Keys.fork in jacoco.Config := true
    )
    
  lazy val s = playJavaSettings ++ Seq(jacoco.settings:_*) ++ jacocoSettings
  
  val commonDependencies = Seq(
      javaCore
      )

  val common = play.Project(
      "common", appVersion, commonDependencies, settings = s, path = file("modules/common")    
  ).settings(
      concurrentRestrictions in Global += Tags.limit(Tags.Test, 1)
  )

  val appDependencies = Seq(
      javaJdbc,
      javaEbean,
      cache
   )
   
   val main = play.Project("play-multi-project-jacoco", appVersion, appDependencies, settings = s).dependsOn(
      common
   ).aggregate(
      common    
   )
}
   