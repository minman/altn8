package com.leapingfrogs.idea.plugins.alternate.action.ui;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.util.IconLoader;
import com.leapingfrogs.idea.plugins.alternate.action.AbstractRegexItem;
import com.leapingfrogs.idea.plugins.alternate.action.AlternateConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
abstract class AbstractDataPanel<I extends AbstractRegexItem> implements DataInterface {
    private JPanel rootComponent;
    private JTable table;
    private JScrollPane tableScrollPane;
    private JPanel toolbar;
    private final List<I> items = new ArrayList<I>();

    /**
     *
     */
    public AbstractDataPanel() {
        initUIComponents();
    }

    /**
     *
     */
    public final JPanel getRootComponent() {
        return rootComponent;
    }

    /**
     * {@inheritDoc}
     */
    public final void pullDataFrom(AlternateConfiguration configuration) {
        items.clear();
        items.addAll(getConfigurationItem(configuration));

        updateTable();
    }

    /**
     * {@inheritDoc}
     */
    public final void pushDataTo(AlternateConfiguration configuration) {
        List<I> configurationFreeRegexItems = getConfigurationItem(configuration);
        configurationFreeRegexItems.clear();
        configurationFreeRegexItems.addAll(items);
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isModified(AlternateConfiguration configuration) {
        return !items.equals(getConfigurationItem(configuration));
    }

    /**
     * called from idea's form designer -> Custom Create Components
     */
    private void createUIComponents() {
        // table
        table = new JTable(createTableModel());
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                // we have to init here with null
                setForeground(null);
                // show as selected only if enabled and never show focused (because only cell is focused...)
                Component c = super.getTableCellRendererComponent(table, value, table.isEnabled() && isSelected, false, row, column);
                // if is enabled and item have error -> set to red (need to be done after getTableCellRendererComponent -> so selected items also will be red...)
                if (table.isEnabled() && items.get(row).hasError()) {
                    setForeground(Color.RED);
                }
                //
                return c;
            }
        });
        table.setPreferredScrollableViewportSize(new Dimension(-1, -1)); // need for +idea9
        table.setRowSelectionAllowed(true);
        table.setColumnSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) { // edit on double click
                    doEdit();
                }
            }
        });
    }

    /**
     * Init our Components
     */
    private void initUIComponents() {
        DefaultActionGroup actionGroup = new DefaultActionGroup();

        // buttonAdd
        actionGroup.add(new AnAction("Add", "Add new item...", IconLoader.getIcon("/general/add.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doAdd();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(enabled);
            }
        });

        // buttonRemove
        actionGroup.add(new AnAction("Remove", "Remove item...", IconLoader.getIcon("/general/remove.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doRemove();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(enabled && table.getSelectedRow() >= 0);
            }
        });

        // buttonEdit
        actionGroup.add(new AnAction("Edit", "Edit item...", IconLoader.getIcon("/actions/editSource.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doEdit();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(enabled && table.getSelectedRow() >= 0);
            }
        });

        // buttonCopy
        actionGroup.add(new AnAction("Copy", "Copy item...", IconLoader.getIcon("/general/copy.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doCopy();
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(enabled && table.getSelectedRow() >= 0);
            }
        });

        actionGroup.addSeparator();

        // buttonMoveUp
        actionGroup.add(new AnAction("Move Up", "Move item up...", IconLoader.getIcon("/actions/moveUp.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doMove(true);
            }

            @Override
            public void update(AnActionEvent e) {
                e.getPresentation().setEnabled(enabled && table.getSelectedRow() > 0);
            }
        });

        // buttonMoveDown
        actionGroup.add(new AnAction("Move Down", "Move item down...", IconLoader.getIcon("/actions/moveDown.png")) {
            @Override
            public void actionPerformed(AnActionEvent e) {
                doMove(false);
            }

            @Override
            public void update(AnActionEvent e) {
                int selectedRow = table.getSelectedRow();
                e.getPresentation().setEnabled(enabled && selectedRow >= 0 && selectedRow < (table.getModel().getRowCount() - 1));
            }
        });

        toolbar.add(ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, actionGroup, true).getComponent());
    }

    private boolean enabled = true;

    /**
     * stupid Swing does not enable/disable child of a container... so we have to do manualy...
     */
    void updateEnabled(boolean enabled) {
        if (this.enabled != enabled) {
            this.enabled = enabled;
            table.setEnabled(enabled);
            table.getTableHeader().setEnabled(enabled);

            // make table looks more common if its disabled...
            table.setBackground(enabled ? UIManager.getColor("Table.background") : UIManager.getColor("TextField.disabledBackground"));
            tableScrollPane.getViewport().setBackground(table.getBackground());
        }
        updateTable();
    }

    /**
     * UI Update Table
     */
    private void updateTable() {
        table.revalidate();
        table.repaint();
    }

    /**
     * @param index
     */
    private void setSelectedRow(int index) {
        if (rowInRange(index)) {
            table.getSelectionModel().setSelectionInterval(index, index);
        } else {
            table.clearSelection();
        }
    }

    /**
     *
     */
    private boolean rowInRange(int row) {
        return row >= 0 && row < table.getModel().getRowCount();
    }

    /**
     *
     */
    private void doAdd() {
        showEditDialog("Add", null, new Runnable<I>() {
            public void run(@NotNull I item) {
                items.add(item);
                setSelectedRow(items.size() - 1);
                updateTable();
            }
        });
    }

    /**
     *
     */
    private void doCopy() {
        int selectedRow = table.getSelectedRow();
        if (rowInRange(selectedRow)) {
            showEditDialog("Copy", items.get(selectedRow), new Runnable<I>() {
                public void run(@NotNull I item) {
                    items.add(item);
                    setSelectedRow(items.size() - 1);
                    updateTable();
                }
            });
        }
    }

    /**
     *
     */
    private void doEdit() {
        final int selectedRow = table.getSelectedRow();
        if (rowInRange(selectedRow)) {
            showEditDialog("Edit", items.get(selectedRow), new Runnable<I>() {
                public void run(@NotNull I item) {
                    items.remove(selectedRow);
                    items.add(selectedRow, item);
                    setSelectedRow(selectedRow);
                    updateTable();
                }
            });
        }
    }

    /**
     *
     */
    private void doRemove() {
        int selectedRow = table.getSelectedRow();
        if (rowInRange(selectedRow)) {
            items.remove(selectedRow);
            setSelectedRow(Math.min(selectedRow, items.size() - 1));
            updateTable();
        }
    }

    /**
     *
     */
    private void doMove(boolean up) {
        int selectedRow = table.getSelectedRow();
        int newPosition = selectedRow + (up ? -1 : 1);
        if (rowInRange(newPosition)) {
            items.add(newPosition, items.remove(selectedRow));
            setSelectedRow(newPosition);
            updateTable();
        }
    }

    /**
     * @return get the actual List
     */
    protected final List<I> getItems() {
        return items;
    }

    /**
     *  Show a Dialog to Edit the Item
     *
     * @param title         additional title (like 'Add', 'Copy' etc.)
     * @param currentItem   current Item to edit (immutable). Maybe null in case of add, else always a item is given
     * @param runnable      run to proceed. else do nothing with it...
     */
    protected abstract void showEditDialog(@NotNull String title, @Nullable I currentItem, @NotNull Runnable<I> runnable);

    /**
     * @param configuration
     * @return                  List of Items from Configuration
     */
    @NotNull
    protected abstract List<I> getConfigurationItem(@NotNull AlternateConfiguration configuration);

    /**
     * @return  Create TableModel for Items
     */
    protected abstract DataTableModel createTableModel();

    /**
     * Runnable for {@link #showEditDialog}
     */
    public static interface Runnable<RI> {
        void run(@NotNull RI item);
    }

    /**
     * DataTableModel for sub-classes
     */
    protected abstract class DataTableModel extends AbstractTableModel {
        public final int getRowCount() {
            return getItems().size();
        }

        public final boolean isCellEditable(int i, int i1) {
            return false;
        }
    }
}
