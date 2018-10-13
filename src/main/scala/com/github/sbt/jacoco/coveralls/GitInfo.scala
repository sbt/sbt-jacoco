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

package com.github.sbt.jacoco.coveralls

import java.io.{File, IOException}

import org.eclipse.jgit.lib.{Constants, ObjectId}
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.storage.file.FileRepositoryBuilder

import scala.collection.JavaConverters._

case class GitInfo(
    hash: String,
    authorName: String,
    authorEmail: String,
    committerName: String,
    committerEmail: String,
    message: String,
    branch: String,
    remotes: Seq[GitRemote])

case class GitRemote(name: String, url: String)

object GitInfo {
  def extract(basedir: File): Option[GitInfo] = {
    try {
      val gitDir = new File(basedir, ".git")

      if (gitDir.isDirectory) {
        val repo = FileRepositoryBuilder.create(gitDir)
        val config = repo.getConfig
        val head = repo.findRef(Constants.HEAD)
        val walk = new RevWalk(repo)
        val commit = walk.parseCommit(head.getObjectId)

        val remotes = config.getSubsections("remote").asScala map { name =>
          GitRemote(name, config.getString("remote", name, "url"))
        }

        val info = GitInfo(
          ObjectId.toString(head.getObjectId),
          commit.getAuthorIdent.getName,
          commit.getAuthorIdent.getEmailAddress,
          commit.getCommitterIdent.getName,
          commit.getCommitterIdent.getEmailAddress,
          commit.getShortMessage,
          repo.getBranch,
          remotes.toSeq
        )

        Some(info)
      } else {
        None
      }
    } catch {
      case _: IOException =>
        // don't have a git repo
        None
    }
  }
}
