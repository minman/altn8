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
package altn8.filechooser;

import altn8.AlternateFileGroup;
import com.intellij.ide.util.gotoByName.GotoFileCellRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.PopupChooserBuilder;
import com.intellij.openapi.wm.ex.WindowManagerEx;
import com.intellij.psi.PsiFile;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @see #prompt(String, List, Project, FileHandler)
 */
public final class AlternateFilePopupChooser {
    /**
     * only static methods
     */
    private AlternateFilePopupChooser() {
    }

    /**
     * Let user choose from a list of files and do something with it. If only one item is present, the file will be processed
     * directly without user prompt. Nothing happens with an emtpy list.
     *
     * @param title          Popup's title
     * @param fileGroups     List of fileGroups
     * @param currentProject
     * @param fileHandler    FileHandler to process choosed files
     */
    public static void prompt(String title, List<AlternateFileGroup> fileGroups, Project currentProject, final FileHandler fileHandler) {
        if (fileGroups != null && !fileGroups.isEmpty()) {
            // if we have only one group with 1 file...
            if (fileGroups.size() == 1 && fileGroups.get(0).getFiles().size() == 1) {
                // ...then open file directly
                fileHandler.processFile(fileGroups.get(0).getFiles().get(0));
            } else {
                // let user choose...

                // list of Objects for out JList
                List<Object> list = new ArrayList<Object>();

                // if we have only 1 group, we dont show title, just adding all PsiFiles
                if (fileGroups.size() == 1) {
                    list.addAll(fileGroups.get(0).getFiles());
                } else {
                    // go thru all groups
                    for (AlternateFileGroup fileGroup : fileGroups) {
                        // add basefilename (will be presented as title) and all files
                        list.add(fileGroup.getBaseFilename());
                        list.addAll(fileGroup.getFiles());
                    }
                }

                final JList valueList = new JList(list.toArray());
                valueList.setCellRenderer(new AlternateCellRenderer(currentProject));
                valueList.setSelectionModel(new AlternateListSelectionModel(list));

                PopupChooserBuilder listPopupBuilder = JBPopupFactory.getInstance().createListPopupBuilder(valueList);
                listPopupBuilder.setTitle(title);
                listPopupBuilder.setItemChoosenCallback(new Runnable() {
                    public void run() {
                        for (Object item : valueList.getSelectedValues()) {
                            if (item instanceof PsiFile) {
                                fileHandler.processFile((PsiFile) item);
                            }
                        }
                    }
                });
                listPopupBuilder.createPopup().showCenteredInCurrentWindow(currentProject);
            }
        }
    }

    /**
     * CellRenderer, renders PsiFile with ideas GotoFileCellRenderer and all other with DefaultListCellRenderer like a title (bgcolor: control)
     */
    static class AlternateCellRenderer extends DefaultListCellRenderer {
        private ListCellRenderer gotoFileCellRenderer;

        AlternateCellRenderer(Project project) {
            this.gotoFileCellRenderer = new GotoFileCellRenderer(WindowManagerEx.getInstanceEx().getFrame(project).getSize().width);
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value instanceof PsiFile) {
                return gotoFileCellRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            } else {
                if (value.toString().length() == 0) {
                    value = " "; // we need a caracter to have correct height
                }
                Component c = super.getListCellRendererComponent(list, value, index, false, cellHasFocus);
                c.setBackground(UIManager.getColor("control"));
                return c;
            }
        }
    }

    /**
     * Selection model accepts only PsiFile for selecting (no group headers)
     */
    static class AlternateListSelectionModel extends DefaultListSelectionModel {
        private List<Object> list;
        private int first = -1;
        private int last = -1;
        private int current0 = -1;
        private int current1 = -1;

        AlternateListSelectionModel(List<Object> list) {
            this.list = list;
            // first psiFile in list
            for (int i = 0, listSize = list.size(); i < listSize; i++) {
                if (list.get(i) instanceof PsiFile){
                    first = i;
                    break;
                }
            }
            // last psiFile in list
            for (int i = list.size() - 1; i >= 0; i--) {
                if (list.get(i) instanceof PsiFile){
                    last = i;
                    break;
                }
            }
        }

        @Override
        public void addSelectionInterval(int index0, int index1) {
            adjustCurrent(index0, index1);
            super.addSelectionInterval(current0, current1);
        }

        @Override
        public void setSelectionInterval(int index0, int index1) {
            adjustCurrent(index0, index1);
            super.setSelectionInterval(current0, current1);
        }

        private void adjustCurrent(int index0, int index1) {
            if (index0 == index1) {
                // single line select
                current0 = calcIndex(current0, index0, true);
                current1 = current0;
            } else {
                // multiline select
                current0 = calcIndex(current0, index0, false);
                current1 = calcIndex(current1, index1, false);
            }
        }

        private int calcIndex(int current, int newIndex, boolean cycle) {
            // we was on first item and go to last
            if (cycle && current == first && (newIndex < current || newIndex == list.size() - 1)) {
                return last;
            }
            // we was on last item and go to first
            if (cycle && current == last && (newIndex > current || newIndex == 0)) {
                return first;
            }
            // next
            if (current < newIndex) {
                current = newIndex;
                while (current < list.size() && !(list.get(current) instanceof PsiFile)) {
                    current++;
                }
            } else if (current > newIndex) {
                // previous
                current = newIndex;
                while (current >= 0 && !(list.get(current) instanceof PsiFile)) {
                    current--;
                }
            }
            return current;
        }
    }
}
