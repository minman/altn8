/*
 * Copyright 2012 The AltN8-Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package altn8;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class AlternateFileGroup implements Comparable<AlternateFileGroup> {
    private String groupId;
    private List<String> baseFilenames = new ArrayList<String>();
    private List<PsiFile> files = new ArrayList<PsiFile>();

    /**
     *
     */
    public AlternateFileGroup(@NotNull String groupId) {
        this.groupId = groupId;
    }

    /**
     * Id of this Group
     */
    @NotNull
    public String getGroupId() {
        return groupId;
    }

    @NotNull
    public String getGroupTitle() {
        if (baseFilenames.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (String baseFilename : baseFilenames) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(baseFilename);
        }
        return sb.toString();
    }

    public void addFile(@NotNull String baseFilename, @NotNull PsiFile psiFile) {
        if (!baseFilenames.contains(baseFilename)) {
            baseFilenames.add(baseFilename);
        }
        files.add(psiFile);
    }

    /**
     * @return List with all files of this group
     */
    public List<PsiFile> getFiles() {
        return files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        AlternateFileGroup that = (AlternateFileGroup) o;

        return groupId.equals(that.groupId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return groupId.hashCode();
    }

    /**
     * {@inheritDoc}
     */
    public int compareTo(AlternateFileGroup o) {
        return groupId.compareTo(o.groupId);
    }
}
