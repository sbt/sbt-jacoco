/*
 * This file is part of jacoco4sbt.
 *
 * Copyright (c) Joachim Hofer & contributors
 * All rights reserved.
 *
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package de.johoop.jacoco4sbt

import sbt._

object JacocoPlugin extends BaseJacocoPlugin {

  object autoImport extends CommonKeys {
    lazy val Jacoco: Configuration = config("jacoco").extend(Test).hide
  }

  import autoImport._

  override protected val pluginConfig: Configuration = Jacoco
  lazy val srcConfig: Configuration = Test

  override def trigger: PluginTrigger = allRequirements
}
