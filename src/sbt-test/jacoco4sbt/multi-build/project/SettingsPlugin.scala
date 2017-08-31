import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin
import de.johoop.jacoco4sbt.JacocoPlugin.jacoco

object SettingsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = AllRequirements
  override def requires: Plugins = JvmPlugin

  override def projectSettings: Seq[Setting[_]] = Seq(
    scalaVersion := (scalaVersion in LocalRootProject).value,
    libraryDependencies ++= (libraryDependencies in LocalRootProject).value
  ) ++ jacoco.settings
}
