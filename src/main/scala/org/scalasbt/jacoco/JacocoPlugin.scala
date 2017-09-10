/*
 * This file is part of sbt-jacoco.
 *
 * Copyright (c) Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.scalasbt.jacoco

import sbt._

object JacocoPlugin extends BaseJacocoPlugin {

  object autoImport extends JacocoKeys {
    lazy val Jacoco: Configuration = config("jacoco").extend(Test).hide
  }

  import autoImport._

  override protected val pluginConfig: Configuration = Jacoco
  lazy val srcConfig: Configuration = Test

  override def trigger: PluginTrigger = allRequirements

  override def projectSettings: Seq[Setting[_]] = super.projectSettings ++ Seq(
    jacoco in Compile := (jacoco in Jacoco).value,
    jacoco in Test := (jacoco in Jacoco).value
  )
}
