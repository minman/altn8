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

import altn8.AlternateGenericFileExtensionRegexItem;
import altn8.AlternateUtils;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @see #showDialog(String, altn8.AlternateGenericFileExtensionRegexItem , Runnable)
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
        fileExtensionTextField.setText(item.fileExtension);
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
        setErrorText(AlternateUtils.toHTML(AlternateGenericFileExtensionRegexItem.validate(fileExtension)));
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
        return AlternateGenericFileExtensionRegexItem.of(fileExtensionTextField.getText());
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
