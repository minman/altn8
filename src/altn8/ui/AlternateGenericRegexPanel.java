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
package altn8.ui;

import altn8.AlternateConfiguration;
import altn8.AlternateGenericFileExtensionRegexItem;
import altn8.AlternateGenericPrefixPostfixRegexItem;
import com.intellij.openapi.ui.Splitter;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

/**
 *
 */
class AlternateGenericRegexPanel implements DataInterface {
    private JPanel rootComponent;
    private JCheckBox activeCheckBox;
    @SuppressWarnings({"UnusedDeclaration"})
    private JPanel dataPanel;
    private JCheckBox caseInsensitiveBasenameCheckBox;
    private AlternateGenericAbstractRegexItemDataPanel prefixItemDataPanel;
    private AlternateGenericAbstractRegexItemDataPanel postfixItemDataPanel;
    private AlternateGenericFileExtensionRegexItemDataPanel fileExtensionItemDataPanel;

    public AlternateGenericRegexPanel() {
        initUIComponents();
    }

    public JPanel getRootComponent() {
        return rootComponent;
    }

    public void pullDataFrom(AlternateConfiguration configuration) {
        activeCheckBox.setSelected(configuration.genericRegexActive);
        caseInsensitiveBasenameCheckBox.setSelected(configuration.caseInsensitiveBasename);
        prefixItemDataPanel.pullDataFrom(configuration);
        postfixItemDataPanel.pullDataFrom(configuration);
        fileExtensionItemDataPanel.pullDataFrom(configuration);
    }

    public void pushDataTo(AlternateConfiguration configuration) {
        configuration.genericRegexActive = activeCheckBox.isSelected();
        configuration.caseInsensitiveBasename = caseInsensitiveBasenameCheckBox.isSelected();
        prefixItemDataPanel.pushDataTo(configuration);
        postfixItemDataPanel.pushDataTo(configuration);
        fileExtensionItemDataPanel.pushDataTo(configuration);
    }

    public boolean isModified(AlternateConfiguration configuration) {
        return activeCheckBox.isSelected() != configuration.genericRegexActive || caseInsensitiveBasenameCheckBox.isSelected() != configuration.caseInsensitiveBasename ||
                prefixItemDataPanel.isModified(configuration) || postfixItemDataPanel.isModified(configuration) || fileExtensionItemDataPanel.isModified(configuration);
    }

    /**
     * called from idea's form designer -> Custom Create Components
     */
    private void createUIComponents() {
        prefixItemDataPanel = new AlternateGenericPrefixRegexItemDataPanel();
        postfixItemDataPanel = new AlternateGenericPostfixRegexItemDataPanel();
        fileExtensionItemDataPanel = new AlternateGenericFileExtensionRegexItemDataPanel();

        Splitter innerSplitter = new Splitter(false, 0.5f);
        innerSplitter.setHonorComponentsMinimumSize(true);

        innerSplitter.setFirstComponent(createItemDataPanel(prefixItemDataPanel, AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX.getText()));
        innerSplitter.setSecondComponent(createItemDataPanel(postfixItemDataPanel, AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX.getText()));

        Splitter outerSplitter = new Splitter(false, 0.67f);
        outerSplitter.setHonorComponentsMinimumSize(true);
        outerSplitter.setFirstComponent(innerSplitter);
        outerSplitter.setSecondComponent(createItemDataPanel(fileExtensionItemDataPanel, "File Extension"));

        //
        dataPanel = outerSplitter;
    }

    private JPanel createItemDataPanel(AbstractDataPanel itemDataPanel, String title) {
        JPanel panel = new JPanel(new GridLayoutManager(1, 1));
        GridConstraints constraints = new GridConstraints();
        constraints.setFill(GridConstraints.FILL_BOTH);
        panel.add(itemDataPanel.getRootComponent(), constraints);
        panel.setBorder(BorderFactory.createTitledBorder(title));
        return panel;
    }

    /**
     * Init our Components
     */
    private void initUIComponents() {
        activeCheckBox.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                updateEnabled(activeCheckBox.isSelected());
            }
        });
        //
        updateEnabled(activeCheckBox.isSelected());
    }

    /**
     * stupid Swing does not enable/disable child of a container... so we have to do manualy...
     */
    private void updateEnabled(boolean enabled) {
        caseInsensitiveBasenameCheckBox.setEnabled(enabled);
        prefixItemDataPanel.updateEnabled(enabled);
        postfixItemDataPanel.updateEnabled(enabled);
        fileExtensionItemDataPanel.updateEnabled(enabled);
    }

    /**
     *
     */
    private static class AlternateGenericFileExtensionRegexItemDataPanel extends AbstractDataPanel<AlternateGenericFileExtensionRegexItem> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected void showEditDialog(@NotNull String title, @Nullable AlternateGenericFileExtensionRegexItem currentItem, @NotNull Runnable<AlternateGenericFileExtensionRegexItem> runnable) {
            AlternateGenericFileExtensionRegexItemDialog.showDialog(title, currentItem == null ? AlternateGenericFileExtensionRegexItem.of("") : currentItem, runnable);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NotNull
        protected List<AlternateGenericFileExtensionRegexItem> getConfigurationItem(@NotNull AlternateConfiguration configuration) {
            return configuration.genericFileExtensionRegexItems;
        }

        private static final String[] TABLECOLUMNS = new String[]{"File Extension"};
        private static final int TABLECOLUMN_FILEEXTENSION = 0;

        /**
         * {@inheritDoc}
         */
        @Override
        protected DataTableModel createTableModel() {
            return new DataTableModel() {
                public int getColumnCount() {
                    return TABLECOLUMNS.length;
                }

                public Object getValueAt(int rowIndex, int columnIndex) {
                    AlternateGenericFileExtensionRegexItem item = getItems().get(rowIndex);
                    switch (columnIndex) {
                        case TABLECOLUMN_FILEEXTENSION:
                            return item.fileExtension;
                    }
                    throw new IllegalArgumentException("Unknown column index: " + columnIndex);
                }

                @Override
                public String getColumnName(int column) {
                    return TABLECOLUMNS[column];
                }
            };
        }
    }

    /**
     *
     */
    private static abstract class AlternateGenericAbstractRegexItemDataPanel extends AbstractDataPanel<AlternateGenericPrefixPostfixRegexItem> {
        private AlternateGenericPrefixPostfixRegexItem.GenericType type;

        protected AlternateGenericAbstractRegexItemDataPanel(AlternateGenericPrefixPostfixRegexItem.GenericType type) {
            this.type = type;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void showEditDialog(@NotNull String title, @Nullable AlternateGenericPrefixPostfixRegexItem currentItem, @NotNull Runnable<AlternateGenericPrefixPostfixRegexItem> runnable) {
            AlternateGenericPrefixPostfixRegexItemDialog.showDialog(title, currentItem == null ? AlternateGenericPrefixPostfixRegexItem.of(type, "", true, "") : currentItem, runnable);
        }

        private static final String[] TABLECOLUMNS = new String[]{"Expression", "Grouping"};
        private static final int TABLECOLUMN_EXPRESSION = 0;
        private static final int TABLECOLUMN_GROUPING = 1;

        /**
         * {@inheritDoc}
         */
        @Override
        protected DataTableModel createTableModel() {
            return new DataTableModel() {
                public int getColumnCount() {
                    return TABLECOLUMNS.length;
                }

                public Object getValueAt(int rowIndex, int columnIndex) {
                    AlternateGenericPrefixPostfixRegexItem item = getItems().get(rowIndex);
                    switch (columnIndex) {
                        case TABLECOLUMN_EXPRESSION:
                            return item.expression;
                        case TABLECOLUMN_GROUPING:
                            return item.grouping;
                    }
                    throw new IllegalArgumentException("Unknown column index: " + columnIndex);
                }

                @Override
                public String getColumnName(int column) {
                    return TABLECOLUMNS[column];
                }
            };
        }
    }

    private static class AlternateGenericPrefixRegexItemDataPanel extends AlternateGenericAbstractRegexItemDataPanel {
        private AlternateGenericPrefixRegexItemDataPanel() {
            super(AlternateGenericPrefixPostfixRegexItem.GenericType.PREFIX);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NotNull
        protected List<AlternateGenericPrefixPostfixRegexItem> getConfigurationItem(@NotNull AlternateConfiguration configuration) {
            return configuration.genericPrefixRegexItems;
        }
    }

    private static class AlternateGenericPostfixRegexItemDataPanel extends AlternateGenericAbstractRegexItemDataPanel {
        private AlternateGenericPostfixRegexItemDataPanel() {
            super(AlternateGenericPrefixPostfixRegexItem.GenericType.POSTFIX);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NotNull
        protected List<AlternateGenericPrefixPostfixRegexItem> getConfigurationItem(@NotNull AlternateConfiguration configuration) {
            return configuration.genericPostfixRegexItems;
        }
    }
}
