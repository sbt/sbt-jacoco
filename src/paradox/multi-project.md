# Multi-Project Builds

For the aggregated multi-project build shown below:

```scala
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
```

Running `sbt jacocoAggregate` will run the unit tests for each sub-project with coverage and then aggregate the
individual reports into a single report in the root project:

```
[info] ------- Jacoco Coverage Report -------
[info]
[info] Lines: 100% (>= required 0.0%) covered, 0 of 3 missed, OK
[info] Instructions: 100% (>= required 0.0%) covered, 0 of 29 missed, OK
[info] Branches: 0% (>= required 0.0%) covered, 0 of 0 missed, OK
[info] Methods: 100% (>= required 0.0%) covered, 0 of 2 missed, OK
[info] Complexity: 100% (>= required 0.0%) covered, 0 of 2 missed, OK
[info] Class: 100% (>= required 0.0%) covered, 0 of 1 missed, OK
[info]
[info] Check /home/example/jacoco-test/common/target/scala-2.12/jacoco/report for detailed report
...
[info] ------- Jacoco Coverage Report -------
[info]
[info] Lines: 100% (>= required 0.0%) covered, 0 of 1 missed, OK
[info] Instructions: 68.75% (>= required 0.0%) covered, 5 of 16 missed, OK
[info] Branches: 0% (>= required 0.0%) covered, 0 of 0 missed, OK
[info] Methods: 50% (>= required 0.0%) covered, 1 of 2 missed, OK
[info] Complexity: 50% (>= required 0.0%) covered, 1 of 2 missed, OK
[info] Class: 50% (>= required 0.0%) covered, 1 of 2 missed, OK
[info]
[info] Check /home/example/jacoco-test/extras/target/scala-2.12/jacoco/report for detailed report
...
[info] ------- Jacoco Aggregate Coverage Report -------
[info]
[info] Lines: 100% (>= required 0.0%) covered, 0 of 4 missed, OK
[info] Instructions: 88.89% (>= required 0.0%) covered, 5 of 45 missed, OK
[info] Branches: 0% (>= required 0.0%) covered, 0 of 0 missed, OK
[info] Methods: 75% (>= required 0.0%) covered, 1 of 4 missed, OK
[info] Complexity: 75% (>= required 0.0%) covered, 1 of 4 missed, OK
[info] Class: 66.67% (>= required 0.0%) covered, 1 of 3 missed, OK
[info]
[info] Check /home/example/jacoco-test/target/scala-2.12/jacoco/report/aggregate for detailed report
```

@@@ note
Due to a limitation in the way that the aggregate report is generated, there no line-by-line source reports are
generated in the aggregate coverage report. These reports can be viewed by opening the sub-project reports.
@@@

## Customising the Aggregate Report

The aggregate report can be customised using the `jacocoAggregateReportSettings` key in the root project:

```scala
lazy val root = project
  .in(file("."))
  .aggregate(
    common,
    extras
  )
  .settings(
    jacocoAggregateReportSettings := JacocoReportSettings(
      title = "Foo Project Coverage",
      formats = Seq(JacocoReportFormats.ScalaHTML)
    )
  )
```
