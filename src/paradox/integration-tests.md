# Integration Tests

_sbt-jacoco_ supports coverage of integration tests using an opt-in plugin which can be enabled by adding the following
to your build config:

```scala
enablePlugins(JacocoItPlugin)
```

Once this has been added you can cover your integration tests using `it:jacoco`. If you have previously run `jacoco` to
cover your unit tests the two coverage reports will get merged into a single report showing you the full coverage.


## Combining Unit and Integration Test Results

---

@@@ warning

_sbt-jacoco_ adds settings to the `IntegrationTest` configuration which get overwritten if

If you have

```scala
configs(IntegrationTest)
Defaults.itSettings
```
@@@


For integration testing enable the the plugin using:

enablePlugins(JacocoItPlugin)
You will also need to remove the following lines from your config if you have them:

configs(IntegrationTest)
Defaults.itSettings
(this is due to a limitation in SBT where it's difficult for plugins to configure themselves for integration tests).

Once you have enabled the integration test plugin you will be able to cover the integration tests using sbt it:jacoco. Unit and integration tests will get merged automatically leaving you with the following reports:

target/scala-2.10/jacoco/report/test/html/index.html
target/scala-2.10/jacoco/report/it/html/index.html
target/scala-2.10/jacoco/report/merged/html/index.html
