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
jacocoCoverallsJobId := sys.env.get("BUILD_ID") // If None, Coveralls sets its own job ID.
jacocoCoverallsRepoToken := "<repo token on coveralls.io>"
```

More settings can found at @ref:[Coveralls Plugin](settings.md#coveralls) settings.

### GitHub Actions + Coveralls
Add `COVERALLS_REPO_TOKEN`  to `Secrets` in your GitHub project at `https://github.com/<organization>/<project>/settings`

Your GitHub Actions workflow yaml (e.g. `.github/workflows/build.yml`) should look like
```yaml
name: Build

on: [push]

jobs:
  build_java_project:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v1
      - name: Build Project
        env:
          COVERALLS_REPO_TOKEN: ${{ secrets.COVERALLS_REPO_TOKEN }}
        run: |
          export CI_BRANCH="${GITHUB_REF#refs/heads/}"
          sbt clean jacoco jacocoCoveralls
``` 
Add the following `jacocoCoveralls*` settings to `build.sbt`

```sbt
lazy val root = (project in file("."))
  .settings(
    jacocoCoverallsServiceName := "github-actions", 
    jacocoCoverallsBranch := sys.env.get("CI_BRANCH"),
    jacocoCoverallsPullRequest := sys.env.get("GITHUB_EVENT_NAME"),
    jacocoCoverallsRepoToken := sys.env.get("COVERALLS_REPO_TOKEN")
  )
```

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

## Codacy

Similar to Codecov, the Codacy reporter script will upload coverage automatically if the XML formatter is enabled.
Check the [documentation](https://support.codacy.com/hc/en-us/articles/207279819-Coverage) or the [GitHub repo](https://github.com/codacy/codacy-coverage-reporter#running-codacy-coverage-reporter) for more information. Example snippets:

```scala
jacocoReportSettings := JacocoReportSettings(
  "Jacoco Coverage Report",
  None,
  JacocoThresholds(),
  Seq(JacocoReportFormats.ScalaHTML, JacocoReportFormats.XML), // note XML formatter
  "utf-8")
```  

With this enabled run the Codacy script after JaCoCo:

```sh
sbt jacoco
bash <(curl -Ls https://coverage.codacy.com/get.sh)
```
