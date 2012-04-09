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
import altn8.AlternateFreeRegexItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.List;

/**
 *
 */
class AlternateFreeRegexPanel implements DataInterface {
    private JPanel rootComponent;
    private JCheckBox activeCheckBox;
    @SuppressWarnings({"UnusedDeclaration"})
    private JPanel dataPanel;
    private AlternateFreeRegexItemDataPanel itemDataPanel;

    public AlternateFreeRegexPanel() {
        initUIComponents();
    }

    public JPanel getRootComponent() {
        return rootComponent;
    }

    public void pullDataFrom(AlternateConfiguration configuration) {
        activeCheckBox.setSelected(configuration.freeRegexActive);
        itemDataPanel.pullDataFrom(configuration);
    }

    public void pushDataTo(AlternateConfiguration configuration) {
        configuration.freeRegexActive = activeCheckBox.isSelected();
        itemDataPanel.pushDataTo(configuration);
    }

    public boolean isModified(AlternateConfiguration configuration) {
        return activeCheckBox.isSelected() != configuration.freeRegexActive ||
                itemDataPanel.isModified(configuration);
    }

    /**
     * called from idea's form designer -> Custom Create Components
     */
    private void createUIComponents() {
        itemDataPanel = new AlternateFreeRegexItemDataPanel();
        //
        dataPanel = itemDataPanel.getRootComponent();
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
        itemDataPanel.updateEnabled(enabled);
    }

    /**
     *
     */
    private static class AlternateFreeRegexItemDataPanel extends AbstractDataPanel<AlternateFreeRegexItem> {
        /**
         * {@inheritDoc}
         */
        @Override
        protected void showEditDialog(@NotNull String title, @Nullable AlternateFreeRegexItem currentItem, @NotNull Runnable<AlternateFreeRegexItem> runnable) {
            AlternateFreeRegexItemDialog.showDialog(title, currentItem == null ? AlternateFreeRegexItem.of("", "") : currentItem, runnable);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NotNull
        protected List<AlternateFreeRegexItem> getConfigurationItem(@NotNull AlternateConfiguration configuration) {
            return configuration.freeRegexItems;
        }

        private static final String[] TABLECOLUMNS = new String[]{"Match Expression", "Replace Expression"};
        private static final int TABLECOLUMN_MATCHEXPRESSION = 0;
        private static final int TABLECOLUMN_REPLACEEXPRESSION = 1;

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
                    AlternateFreeRegexItem item = getItems().get(rowIndex);
                    switch (columnIndex) {
                        case TABLECOLUMN_MATCHEXPRESSION:
                            return item.matchExpression;
                        case TABLECOLUMN_REPLACEEXPRESSION:
                            return item.replaceExpression;
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
}
