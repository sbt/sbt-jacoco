package com.github.sbt

import com.github.sbt.jacoco.data.ProjectData
import sbt.ResolvedProject

package object jacoco {
  private[jacoco] def projectData(project: ResolvedProject): ProjectData = {
    ProjectData(project.id)
  }
}
