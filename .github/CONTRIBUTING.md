# Contributing

When you find a bug in jacoco4sbt we want to hear about it. Your bug reports play an important part in making this
plugin more reliable and usable.

Effective bug reports are more likely to be fixed. These guidelines explain how to write such reports and pull requests.

## Before Reporting an Issue

* Make sure that you are running the latest version of SBT (1.x) and the jacoco4sbt plugin.
* Check the open [issues](https://github.com/sbt/jacoco4sbt/issues) and
  [pull requests](https://github.com/sbt/jacoco4sbt/pulls) for anything similar. If there is already an open issue
  and you have additional information please add it (comments such as +1 aren't helpful).

## How to Report an Issue

It is important when opening a new issue to include as much information as possible including:

* How to reproduce the issue:
  * What SBT tasks did you run before jacoco?
  * Does it always happen?
  * Does running `clean` beforehand solve it?
* Details of your environment:
  * Version of SBT.
  * Version of the jacoco4sbt plugin.
  * Java version.
  * Scala versions (all versions if cross-building).
* A link to a repo contining your project _or_
* A link to a Gist/Pastebin with a test case reproducing the issue.

## How to Submit a Pull Request

We welcome code contributions to jacoco4sbt. To make it easier for us to include your contributions please ensure the
following before creating a pull request:

* Your branch is up to date with `main`.
* All unit tests and integration tests pass.

When opening a pull request please including any relevant information - such as _"fixes issue #x"_ or _"adds new feature y"_.
