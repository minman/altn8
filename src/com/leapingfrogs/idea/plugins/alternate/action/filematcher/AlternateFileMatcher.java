package com.leapingfrogs.idea.plugins.alternate.action.filematcher;

import org.jetbrains.annotations.NotNull;

/**
 *
 */
public interface AlternateFileMatcher {
    /**
     * @return true, if filename matches and file should be added
     */
    boolean matches(@NotNull String filename);

    /**
     * @return baseFilename for grouping
     */
    @NotNull
    String getBaseFilename(@NotNull String filename);
}
