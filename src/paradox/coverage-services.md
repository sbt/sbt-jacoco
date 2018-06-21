# Coverage Services

Examples of uploading to [Coveralls](https://coveralls.io/) and [Codecov](https://codecov.io/gh) can be found in the
example project: [stringbean/sbt-jacoco-example](https://github.com/stringbean/sbt-jacoco-example)

## Coveralls

If you have a public project built with Travis-CI you will just need to enable the Coveralls plugin:

```scala
enablePlugins(JacocoCoverallsPlugin)
```

Then run `sbt jacocoCoveralls` to upload the results to Coveralls:

```
[info] Uploading coverage to coveralls.io...
[info] Upload complete
```

For private projects you will need to set a few more settings:

```scala
jacocoCoverallsServiceName := "jenkins"
jacocoCoverallsJobId := sys.env("BUILD_ID")
jacocoCoverallsRepoToken := "<repo token on coveralls.io>"
```

More settings can found at @ref:[Coveralls Plugin](settings.md#coveralls) settings.

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

With this enabled run the Codecov script after JaCoCo:

```sh
sbt jacoco
bash <(curl -s https://codecov.io/bash)
```
