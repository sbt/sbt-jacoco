---
toc.maxDepth: 3
page.subheaders: true
---

# Settings Reference

## Settings

### Common

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

#### jacocoMergedDataFile

* **Description:** Execution data file contain unit test and integration test data.
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

* **Description:** whether to merge the unittest and integration test reports.
* **Accepts:** `Boolean`
* **Default:** `true`

## Types

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
