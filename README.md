# sbt-jacoco - Code Coverage via JaCoCo in sbt

[![Build Status](https://travis-ci.org/sbt/sbt-jacoco.svg?branch=master)](https://travis-ci.org/sbt/sbt-jacoco)
[![SBT 0.13 version](https://img.shields.io/badge/sbt_0.13-3.0.0-blue.svg)](https://bintray.com/stringbean/sbt-plugins/sbt-jacoco)
[![SBT 1.0 version](https://img.shields.io/badge/sbt_1.0-3.0.0-blue.svg)](https://bintray.com/stringbean/sbt-plugins/sbt-jacoco)

This is an [sbt](http://scala-sbt.org/) plugin for code coverage analysis via [JaCoCo](http://www.eclemma.org/jacoco/).

Install the plugin by adding the following to `project/plugins.sbt`:

```scala
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.0.0")
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

See [Wiki](https://github.com/sbt/sbt-jacoco/wiki) for details on configuration options.

## Contributors

Many thanks to the following for their awesome contributions:

* [Alexey Pismenskiy](https://github.com/apismensky)
* [Andreas Flierl](https://bitbucket.org/asflierl)
* [Jacek Laskowski](https://github.com/jaceklaskowski)
* [Jason Zaugg](https://github.com/retronym)
* [Jerry Lin](https://github.com/linjer)
* [Joost den Boer](https://bitbucket.org/diversit)
* [Michael Schleichardt](https://github.com/schleichardt)
* [Patrick Mahoney](https://bitbucket.org/paddymahoney)
* [Wei Chen](https://github.com/wchen9911)
