// Copyright 2008-2010 Victor Iacoban
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software distributed under
// the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
// either express or implied. See the License for the specific language governing permissions and
// limitations under the License.
package org.zmlx.hg4idea.command;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.HashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.zmlx.hg4idea.HgRevisionNumber;
import org.zmlx.hg4idea.execution.HgCommandExecutor;
import org.zmlx.hg4idea.execution.HgCommandResult;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HgTagBranchCommand {

  private static final Pattern BRANCH_LINE = Pattern.compile("(.+)\\s([0-9]+):([0-9a-f]+).*");
  private static final int NAME_INDEX = 1;
  private static final int REVISION_INDEX = 2;
  private static final int CHANGESET_INDEX = 3;

  private final Project project;
  private final VirtualFile repo;

  public HgTagBranchCommand(Project project, @NotNull VirtualFile repo) {
    this.project = project;
    this.repo = repo;
  }

  @Nullable
  public String getCurrentBranch() {
    final HgCommandExecutor executor = new HgCommandExecutor(project);
    executor.setSilent(true);
    HgCommandResult result = executor.executeInCurrentThread(repo, "branch", null);
    if (result == null) {
      return null;
    }
    List<String> output = result.getOutputLines();
    if (output == null || output.isEmpty()) {
      return null;
    }
    return output.get(0).trim();
  }

  public HgCommandResult collectBranches() {
    return new HgCommandExecutor(project).executeInCurrentThread(repo, "branches", null);
  }

  public HgCommandResult collectTags() {
    return new HgCommandExecutor(project).executeInCurrentThread(repo, "tags", null);
  }

  public HgCommandResult collectBookmarks() {
    return new HgCommandExecutor(project).executeInCurrentThread(repo, "bookmarks", null);
  }

  public static List<HgTagBranch> parseResult(@NotNull HgCommandResult result) {
    List<HgTagBranch> branches = new LinkedList<HgTagBranch>();
    for (final String line : result.getOutputLines()) {
      Matcher matcher = BRANCH_LINE.matcher(line);
      if (matcher.matches()) {
        HgRevisionNumber hgRevisionNumber = HgRevisionNumber.getInstance(
          matcher.group(REVISION_INDEX), matcher.group(CHANGESET_INDEX)
        );
        branches.add(new HgTagBranch(matcher.group(NAME_INDEX).trim(), line.trim(), hgRevisionNumber));
      }
    }
    return branches;
  }

  @NotNull
  public static Set<String> collectNames(@NotNull HgCommandResult result) {
    Set<String> branches = new HashSet<String>();
    for (final String line : result.getOutputLines()) {
      Matcher matcher = BRANCH_LINE.matcher(line);
      if (matcher.matches()) {
        branches.add(matcher.group(NAME_INDEX).trim());
      }
    }
    return branches;
  }
}
