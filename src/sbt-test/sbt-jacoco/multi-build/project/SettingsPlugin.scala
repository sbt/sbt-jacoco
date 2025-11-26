import sbt.*
import sbt.Keys.*
import sbt.plugins.JvmPlugin

object SettingsPlugin extends AutoPlugin {
  override def trigger: PluginTrigger = AllRequirements
  override def requires: Plugins = JvmPlugin

  override def projectSettings: Seq[Setting[?]] = Seq(
    scalaVersion := (LocalRootProject / scalaVersion).value,
    libraryDependencies ++= (LocalRootProject / libraryDependencies).value
  )
}
