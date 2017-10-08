# Integration Tests

_sbt-jacoco_ supports coverage of integration tests using an opt-in plugin which can be enabled by adding the following
to your build config:

```scala
enablePlugins(JacocoItPlugin)
```

Once this has been added you can cover your integration tests using `it:jacoco`. If you have previously run `jacoco` to
cover your unit tests the two coverage reports will get merged into a single report showing you the full coverage.


@@@ warning
The `JacocoItPlugin` adds settings to the `IntegrationTest` configuration which get overwritten if you have the
following in your build:

```scala
configs(IntegrationTest)
Defaults.itSettings
```

These get added automatically by the `JacocoItPlugin` in a way that they don't overwrite the extra settings.
@@@
