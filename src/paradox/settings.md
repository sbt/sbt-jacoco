---
toc.maxDepth: 3
page.subheaders: true
---

# Settings Reference

## Settings

### Common

These are apply to both unit and integration tests.

#### jacocoDirectory

* **Description:** Where JaCoCo should store its execution data and reports.
* **Accepts:** `java.io.File`
* **Default:** `crossTarget / "jacoco"`

#### jacocoReportDirectory

* **Description:** Where JaCoCo should output reports to.
* **Accepts:** `java.io.File`
* **Default:** `jacocoDirectory / "report"`

#### jacocoDataFile

* **Description:** Execution data output file.
* **Accepts:** `java.io.File`
* **Default:** `jacocoDirectory / "data" / "jacoco.exec"`

#### jacocoSourceSettings

* **Description:** Input source code settings (encoding etc) for reporting.
* **Accepts:** [JacocoSourceSettings](#jacocosourcesettings)
* **Default:**

```scala
JacocoSourceSettings(2, "utf-8")
```

#### jacocoReportSettings

* **Description:** Settings for JaCoCo report (format, title etc).
* **Accepts:** [JacocoReportSettings](#jacocoreportsettings)
* **Default:**

Unit tests:
```scala
JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML),
  "utf-8")
```

Integration tests:
```scala
JacocoReportSettings(
  "Jacoco Integration Test Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML),
  "utf-8")
```

#### jacocoIncludes

* **Description:** Glob patterns specifying which classes to cover.
* **Accepts:** `Seq[String]`
* **Default:** `**/*` (all classes)

@@@ note
`jacocoExcludes` overrides `jacocoIncludes`.
@@@

#### jacocoExcludes

* **Description:** Glob patterns specifying which classes not to cover.
* **Accepts:** `Seq[String]`
* **Default:** none

#### jacocoInstrumentedDirectory

* **Description:** Directory containing the instrumented classes.
* **Accepts:** `java.io.File`
* **Default:** `jacocoDirectory / "instrumented-classes"`

### Multi-Project Tests

These should be defined in the root project of a multi-project build and control the way that reports for sub-projects
should be aggregated.

#### jacocoAggregateReportSettings

* **Description:** Settings for aggregate JaCoCo report (format, title etc).
* **Accepts:** [JacocoReportSettings](#jacocoreportsettings)
* **Default:**

```scala
JacocoReportSettings(
  "Jacoco Merged Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML),
  "utf-8")
```

### Integration Tests

These are only defined for integration tests and configure merging of unit and integration results.

#### jacocoMergedDataFile

* **Description:** Execution data file containing combined unit test and integration test data.
* **Accepts:** `java.io.File`
* **Default:** `jacocoDirectory / "jacoco-it.exec"`

#### jacocoMergedReportSettings

* **Description:** Settings for merged JaCoCo report (format, title etc).
* **Accepts:** [JacocoReportSettings](#jacocoreportsettings)
* **Default:**

```scala
JacocoReportSettings(
  "Jacoco Merged Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML),
  "utf-8")
```

#### jacocoAutoMerge

* **Description:** Whether to merge the unit and integration test reports.
* **Accepts:** `Boolean`
* **Default:** `true`

### Coveralls

These are only defined if the `JacocoCoverallsPlugin` is enabled.

#### jacocoCoverallsServiceName

* **Description:** Name of the CI service running this build.
* **Accepts:** `String`
* **Default:** `travis-ci`

@@@ note
If running on Travis Pro this should be set to `travis-pro`.
@@@

#### jacocoCoverallsJobId

* **Description:** Unique build identifier for this build
* **Accepts:** `String`
* **Default:** `TRAVIS_JOB_ID` environment variable

#### jacocoCoverallsBuildNumber

* **Description:** Human readable build number
* **Accepts:** `Option[String]`
* **Default:** none (defaults to auto-incremented number)

#### jacocoCoverallsPullRequest

* **Description:** ID of the current pull request that triggered the build.
* **Accepts:** `Option[String]`
* **Default:** none

#### jacocoCoverallsRepoToken

* **Description:** Coveralls repo secret key.
* **Accepts:** `Option[String]`
* **Default:** none (auto detected for public repos)

## Types

All types are automatically imported in an `.sbt` based build file and can be imported into `.scala` based builds using:

```scala
import com.github.sbt.jacoco.JacocoPlugin.autoImport._
```

Each type has `.withXXX` methods and default values defined for all parameters giving you a choice of ways to configure:

```scala
jacocoReportSettings := JacocoReportSettings(title = "Report Title", formats = Seq(JacocoReportFormats.HTML))
// or
jacocoReportSettings := JacocoReportSettings()
  .withTitle("Report Title")
  .withFormats(Seq(JacocoReportFormats.HTML))
```

### JacocoSourceSettings

Properties:

* `tabWidth`: tab width of source files.
* `fileEncoding`: file encoding of source files.

### JacocoReportSettings

Properties:

* `title`: title of the report.
* `subDirectory`: sub-directory under `jacocoReportDirectory` to store the report.
* `thresholds`: required coverage levels.
* `formats`: list of report fomats to use.
* `fileEncoding`: file encoding to use for reports.

### JacocoReportFormats

* `ScalaHTML` - Enhanced version of the standard JaCoCo HTML report that supports Scala language constructs.
* `HTML`
* `XML`
* `CSV`
