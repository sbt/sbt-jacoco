organization := "com.example"

scalaVersion := "2.12.21"

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.20" % "test"
)

lazy val common = project
  .in(file("common"))

lazy val extras = project
  .in(file("extras"))

lazy val root = project
  .in(file("."))
  .aggregate(
    common,
    extras
  )
  .settings(
    publish := {},
    publishLocal := {},
    Test / skip := true
  )

def projectCoverageDirectories(state: State): Seq[File] = {
  val extracted = Project.extract(state)
  Seq(
    extracted.get(common / Test / jacocoDirectory),
    extracted.get(extras / Test / jacocoDirectory)
  )
}

def assertFileExists(file: File): Unit =
  assert(file.isFile(), s"Expected coverage file: $file")

commands ++= Seq(
  Command.command("checkProjectReports") { state =>
    projectCoverageDirectories(state).foreach { directory =>
      assertFileExists(directory / "data" / "jacoco.exec")
      assertFileExists(directory / "report" / "html" / "index.html")
    }
    state
  },
  Command.command("checkNoAggregateReport") { state =>
    val directory = Project.extract(state).get(Test / jacocoDirectory) / "report" / "aggregate"
    assert(!directory.exists(), s"Unexpected aggregate report: $directory")
    state
  },
  Command.command("checkAggregateReport") { state =>
    projectCoverageDirectories(state).foreach { directory =>
      assertFileExists(directory / "data" / "jacoco.exec")
    }
    val directory = Project.extract(state).get(Test / jacocoDirectory)
    assertFileExists(directory / "report" / "aggregate" / "html" / "index.html")
    state
  }
)
