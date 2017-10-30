# Coverage Services

Examples of uploading to [Coveralls](https://coveralls.io/) and [Codecov](https://codecov.io/gh) can be found in the
example project: [stringbean/sbt-jacoco-example](https://github.com/stringbean/sbt-jacoco-example)

## Coveralls

Enable the Coveralls plugin:

```scala
enablePlugins(JacocoCoverallsPlugin)
```

Then run `sbt jacocoCoveralls` to upload the results to Coveralls:

```
[info] Upload complete
```

<!-- TODO extra config -->

## Codecov

The Codecov uploader script will upload coverage automatically if the XML formatter is enabled. For example:

```scala
jacocoReportSettings := JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
  "utf-8")
```

With this enabled run the Codecov script:

```shell
bash <(curl -s https://codecov.io/bash)
```
