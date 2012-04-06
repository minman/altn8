package com.leapingfrogs.idea.plugins.alternate.action.filechooser;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * Handler for processing files in {@link AlternateFilePopupChooser}
 */
public interface FileHandler {
    /**
     * @param psiFile   choosed file to process
     */
    void processFile(@NotNull PsiFile psiFile);
}
