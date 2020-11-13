# sbt-jacoco - Code Coverage via JaCoCo in sbt

[![Build Status](https://github.com/sbt/sbt-jacoco/workflows/CI/badge.svg)](https://github.com/sbt/sbt-jacoco/actions?workflow=CI)
[![Release Status](https://github.com/sbt/sbt-jacoco/workflows/Release/badge.svg)](https://github.com/sbt/sbt-jacoco/actions?workflow=Release)
[![Build Status](https://travis-ci.org/sbt/sbt-jacoco.svg?branch=master)](https://travis-ci.org/sbt/sbt-jacoco)
[![Codacy Grade](https://img.shields.io/codacy/grade/2336303da07d41ba960ec769dfec0a74.svg?label=codacy)](https://www.codacy.com/app/stringbean/sbt-jacoco)
[![SBT 0.13 version](https://img.shields.io/badge/sbt_0.13-3.2.0-blue.svg)](https://bintray.com/stringbean/sbt-plugins/sbt-jacoco)
[![SBT 1.0 version](https://img.shields.io/badge/sbt_1.0-3.2.0-blue.svg)](https://bintray.com/stringbean/sbt-plugins/sbt-jacoco)

This is an [sbt](http://scala-sbt.org/) plugin for code coverage analysis via [JaCoCo](http://www.eclemma.org/jacoco/).
Supports uploading results to [Coveralls](https://coveralls.io), [Codecov](https://codecov.io) and [Codacy](https://www.codacy.com/).

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

See the [docs](http://scala-sbt.org/sbt-jacoco) for details on configuration options.
