import sbt._
import sbt.Keys._
import sbt.plugins.JvmPlugin

object SettingsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = AllRequirements
  override def requires: Plugins = JvmPlugin

  override def projectSettings: Seq[Setting[_]] = Seq(
    scalaVersion := (scalaVersion in LocalRootProject).value,
    libraryDependencies ++= (libraryDependencies in LocalRootProject).value
  )
}
