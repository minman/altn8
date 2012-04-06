package com.leapingfrogs.idea.plugins.alternate.action;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentIterator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.leapingfrogs.idea.plugins.alternate.action.filechooser.AlternateFilePopupChooser;
import com.leapingfrogs.idea.plugins.alternate.action.filechooser.FileHandler;
import com.leapingfrogs.idea.plugins.alternate.action.filematcher.AlternateFileMatcher;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Our main action
 */
public class AlternateFileAction extends AnAction {
    private final AlternateConfiguration configuration;

    /**
     *
     */
    public AlternateFileAction() {
        this.configuration = ApplicationManager.getApplication().getComponent(AlternateApplicationComponent.class);
    }

    private static VirtualFile getCurrentFile(AnActionEvent e) {
        return DataKeys.VIRTUAL_FILE.getData(e.getDataContext());
    }

    private static Project getProject(AnActionEvent e) {
        return DataKeys.PROJECT.getData(e.getDataContext());
    }

    private static Module getModule(AnActionEvent e) {
        return DataKeys.MODULE.getData(e.getDataContext());
    }

    private static Editor getEditor(AnActionEvent e) {
        return DataKeys.EDITOR.getData(e.getDataContext());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile currentFile = getCurrentFile(e);
        if (currentFile != null) {
            Project currentProject = getProject(e);
            // find these in project
            List<AlternateFileGroup> fileGroups = findFiles(currentFile, currentProject, getModule(e));
            if (fileGroups.isEmpty()) {
                // nothing found
                HintManager.getInstance().showInformationHint(getEditor(e), "No corresponding file(s) found");
            } else {
                // open these...
                AlternateFilePopupChooser.prompt("Select the file(s) to open", fileGroups, currentProject, new FileHandler() {
                    public void processFile(@NotNull PsiFile psiFile) {
                        psiFile.navigate(true);
                    }
                });
            }
        }
    }

    /**
     * Find all corresponding files.<br>
     * If we found at minimunm one file in module, only module-files are listet. Else project files.
     */
    private List<AlternateFileGroup> findFiles(final VirtualFile currentFile, final Project project, final Module module) {
        final Map<String, AlternateFileGroup> projectWorkMap = new HashMap<String, AlternateFileGroup>();
        final Map<String, AlternateFileGroup> moduleWorkMap = new HashMap<String, AlternateFileGroup>();
        final String currentFilename = currentFile.getName();

        // get all fileMatchers
        final List<AlternateFileMatcher> fileMatchers = configuration.getFileMatchers(currentFilename);
        if (!fileMatchers.isEmpty()) {
            // iterate thru files
            final ProjectFileIndex projectFileIndex = ProjectRootManager.getInstance(project).getFileIndex();
            projectFileIndex.iterateContent(new ContentIterator() {
                private PsiManager psiManager = PsiManager.getInstance(project);
                public boolean processFile(VirtualFile fileOrDir) {
                    // if not a directory
                    if (!fileOrDir.isDirectory()) {
                        // and not currentFile...
                        if (!currentFilename.equals(fileOrDir.getName()) || !currentFile.getPath().equals(fileOrDir.getPath())) {
                            // iterate thru matchers and test...
                            for (AlternateFileMatcher fileMatcher : fileMatchers) {
                                if (fileMatcher.matches(fileOrDir.getName())) {
                                    PsiFile psiFile = psiManager.findFile(fileOrDir);
                                    if (psiFile != null) {
                                        Map<String, AlternateFileGroup> workMap = module.equals(projectFileIndex.getModuleForFile(fileOrDir)) ? moduleWorkMap : projectWorkMap;
                                        // add to module or project group
                                        String baseFilename = fileMatcher.getBaseFilename(fileOrDir.getName());
                                        AlternateFileGroup group = workMap.get(baseFilename);
                                        if (group == null) {
                                            group = new AlternateFileGroup(baseFilename);
                                            workMap.put(baseFilename, group);
                                        }
                                        group.getFiles().add(psiFile);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    return true;
                }
            });
        }

        // put all groups from workMap into a list (if moduleItems are presented, only moduleItems will be added, else projectItems)
        List<AlternateFileGroup> result = new ArrayList<AlternateFileGroup>(!moduleWorkMap.isEmpty() ? moduleWorkMap.values() : projectWorkMap.values());
        // sort (by baseFilename)
        Collections.sort(result);
        // move group with same basefilename like current to top
        String currentBaseFilename = null;
        for (AlternateFileMatcher fileMatcher : fileMatchers) {
            if (fileMatcher.matches(currentFilename)) {
                currentBaseFilename = fileMatcher.getBaseFilename(currentFilename);
                break;
            }
        }
        if (currentBaseFilename != null && currentBaseFilename.length() > 0) {
            for (int i = 0, resultSize = result.size(); i < resultSize; i++) {
                AlternateFileGroup fileGroup = result.get(i);
                if (fileGroup.getBaseFilename().equals(currentBaseFilename)) {
                    if (i > 0) {
                        result.add(0, result.remove(i));
                    }
                    break;
                }
            }
        }
        // move group with no basefilename to bottom
        for (int i = 0, resultSize = result.size(); i < resultSize; i++) {
            AlternateFileGroup fileGroup = result.get(i);
            if (fileGroup.getBaseFilename().length() == 0) {
                if (i < result.size() - 1) {
                    result.add(result.remove(i));
                }
                break;
            }
        }

        return result;
    }
}
