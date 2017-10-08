# Getting Started

## Setup

Install the plugin by adding the following to `project/plugins.sbt`:

```scala
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "<version>")
```

And then execute the plugin with `sbt jacoco`. This will instrument and run the unit tests and output the coverage
metrics:

```
[info] ------- Jacoco Coverage Report --------
[info]
[info] Lines: 66.67% (>= required 0.0%) covered, 2 of 6 missed, OK
[info] Instructions: 83.54% (>= required 0.0%) covered, 13 of 79 missed, OK
[info] Branches: 0% (>= required 0.0%) covered, 0 of 0 missed, OK
[info] Methods: 57.14% (>= required 0.0%) covered, 3 of 7 missed, OK
[info] Complexity: 57.14% (>= required 0.0%) covered, 3 of 7 missed, OK
[info] Class: 50% (>= required 0.0%) covered, 2 of 4 missed, OK
[info]
[info] Check /home/example/jacoco-test/target/scala-2.11/jacoco/report for detailed report
```

A detailed HTML report will also be generated in the directory shown that includes line level details of coverage.

## Setting Minimum Coverage Levels

Minimum code coverage levels can be defined so that the build will fail if the requirements are not met:

```scala
jacocoReportSettings := JacocoReportSettings()
  .withThresholds(
    JacocoThresholds(
      instruction = 80,
      method = 100,
      branch = 100,
      complexity = 100,
      line = 90,
      clazz = 100)
  )
```

If the coverage does not meet the thresholds defined the build will fail and the coverge message will highlight the
failed thresholds:

```
[info] ------- Jacoco Coverage Report -------
[info]
[info] Lines: 66.67% (< required 90.0%) covered, 1 of 3 missed, NOK
[info] Instructions: 56.52% (< required 80.0%) covered, 10 of 23 missed, NOK
[info] Branches: 0% (< required 100.0%) covered, 0 of 0 missed, NOK
[info] Methods: 50% (< required 100.0%) covered, 2 of 4 missed, NOK
[info] Complexity: 50% (< required 100.0%) covered, 2 of 4 missed, NOK
[info] Class: 100% (>= required 100.0%) covered, 1 of 2 missed, OK
[info]
[info] Check /home/example/jacoco-test/scala-2.10/jacoco/report for detailed report
[info]
[error] Required coverage is not met
```

@@@ note
By default the minimum coverage levels are set to 0%.
@@@
