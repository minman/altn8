package com.leapingfrogs.idea.plugins.alternate.action.ui;

import com.intellij.openapi.ui.DialogWrapper;
import com.leapingfrogs.idea.plugins.alternate.action.AlternateGenericFileExtensionRegexItem;
import com.leapingfrogs.idea.plugins.alternate.action.AlternateUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @see #showDialog(String, AlternateGenericFileExtensionRegexItem, Runnable)
 */
final class AlternateGenericFileExtensionRegexItemDialog extends DialogWrapper {
    private JPanel topPanel;
    private JTextField fileExtensionTextField;

    /**
     * @param title
     * @param item
     */
    private AlternateGenericFileExtensionRegexItemDialog(@NotNull String title, @NotNull AlternateGenericFileExtensionRegexItem item) {
        super(true);
        setTitle("File Extension: " + title);
        init();
        // fileExtensionTextField
        fileExtensionTextField.setText(item.getFileExtension());
        fileExtensionTextField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                fileExtensionTextField.select(0, fileExtensionTextField.getText().length());
            }
        });
        fileExtensionTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                changed();
            }
        });
        //
        changed();
    }

    private void changed() {
        String fileExtension = fileExtensionTextField.getText();
        // OK enabled if we have input...
        setOKActionEnabled(fileExtension.length() > 0);
        // Show errors (Error ist shown in html, so we convert our Messege to html)
        setErrorText(AlternateUtils.toHTML(AlternateGenericFileExtensionRegexItem.getErrorText(fileExtension)));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected JComponent createCenterPanel() {
        return topPanel;
    }

    /**
     * @return get current item from entries
     */
    @NotNull
    private AlternateGenericFileExtensionRegexItem getItem() {
        return new AlternateGenericFileExtensionRegexItem(fileExtensionTextField.getText());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getPreferredFocusedComponent() {
        return fileExtensionTextField;
    }

    /**
     * 
     */
    public static void showDialog(@NotNull String title, @NotNull AlternateGenericFileExtensionRegexItem currentItem, @NotNull AbstractDataPanel.Runnable<AlternateGenericFileExtensionRegexItem> runnable) {
        AlternateGenericFileExtensionRegexItemDialog dialog = new AlternateGenericFileExtensionRegexItemDialog(title, currentItem);
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            AlternateGenericFileExtensionRegexItem newItem = dialog.getItem();
            if (!newItem.equals(currentItem) && !newItem.isEmpty()) {
                runnable.run(newItem);
            }
        }
    }
}
