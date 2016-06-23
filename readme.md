# jacoco4sbt - Code Coverage via JaCoCo in sbt

This is an [sbt](http://scala-sbt.org/) and [Typesafe Activator](https://typesafe.com/activator) plugin for code coverage analysis via [JaCoCo](http://www.eclemma.org/jacoco/).

Install the plugin by adding the following to `project/plugins.sbt`:

    addSbtPlugin("de.johoop" % "jacoco4sbt" % "2.1.6")

and then in `build.sbt`:

    jacoco.settings

Execute the plugin with `sbt jacoco:cover`.

See [Wiki](https://github.com/sbt/jacoco4sbt/wiki) for more details.

## Build Status

[![Build Status](https://travis-ci.org/sbt/jacoco4sbt.svg?branch=master)](https://travis-ci.org/sbt/jacoco4sbt)

## Change Log
* *2.1.6*
    * Updated to JaCoCo version 0.7.1 (fixing [#22](https://github.com/sbt/jacoco4sbt/issues/22))
    * Fixed integration test settings (fixing [#13](https://github.com/sbt/jacoco4sbt/issues/13))
    * Updated sbt to 0.13.5 and other small fixes

* *2.1.5*
    * Added report summary to the build output
    * Added check for required coverage (fixing [#14](https://github.com/sbt/jacoco4sbt/issues/14))
    * Updated to JaCoCo version 0.7.0 (fixing [#18](https://github.com/sbt/jacoco4sbt/issues/18))

* *2.1.4*
    * Fixed a bad regression ([#8](https://github.com/sbt/jacoco4sbt/issues/8)) and added tests so that it doesn't happen again
    * Made code coverage work with forked tests ([#3](https://github.com/sbt/jacoco4sbt/issues/3) again,
      [#6](https://github.com/sbt/jacoco4sbt/issues/6) and
      [#9](https://github.com/sbt/jacoco4sbt/issues/9)) - this should make life
      a lot easier for [Play2](http://playframework.com) users

* *2.1.3*
    * Fixed [#3](https://github.com/sbt/jacoco4sbt/issues/3) (working directory in JaCoCo configuration)
    * Updated to latest JaCoCo version 0.6.4

* *2.1.2*
    * Added a Scala-specific report format (contributed by Jason)

* *2.1.1*

    * Updated for sbt 0.13.0 (final)

* *2.1.0*

    * Updated to work with sbt 0.13.x (contributed by Patrick)
    * Updated to latest JaCoCo version 0.6.3
    
* *2.0.0* (contributed by Joost)

    * Integration testing
    * (Optional) merging coverage of unit and integration tests
    * `clean` key for cleaning the JaCoCo output directory selectively
    
* *1.2.2* (contributed by Andreas)

    * Includes and excludes

## Contributors

Many thanks to
[Alexey Pismenskiy](https://github.com/apismensky),
[Andreas Flierl](https://bitbucket.org/asflierl),
[Jacek Laskowski](https://github.com/jaceklaskowski),
[Jason Zaugg](https://github.com/retronym),
[Jerry Lin](https://github.com/linjer),
[Joost den Boer](https://bitbucket.org/diversit),
[Michael Schleichardt](https://github.com/schleichardt),
[Patrick Mahoney](https://bitbucket.org/paddymahoney) and
[Wei Chen](https://github.com/wchen9911)
for their awesome contributions!

## License

This program and the accompanying materials are made available under the terms of the **Eclipse Public License v1.0** which accompanies this distribution, and is available at http://www.eclipse.org/legal/epl-v10.html
