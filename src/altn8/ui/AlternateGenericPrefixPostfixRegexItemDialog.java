package altn8.ui;

import altn8.AlternateGenericPrefixPostfixRegexItem;
import altn8.AlternateUtils;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * @see #showDialog(String, altn8.AlternateGenericPrefixPostfixRegexItem , Runnable)
 */
final class AlternateGenericPrefixPostfixRegexItemDialog extends DialogWrapper {
    private JPanel topPanel;
    private AlternateGenericPrefixPostfixRegexItem.GenericType type;
    private JTextField expressionTextField;
    private JCheckBox checkBoxGrouping;

    /**
     * @param title
     * @param item
     */
    private AlternateGenericPrefixPostfixRegexItemDialog(@NotNull String title, @NotNull AlternateGenericPrefixPostfixRegexItem item) {
        super(true);
        setTitle("Generic " + item.getType().getText() + " RegEx: " + title);
        init();
        // expressionTextField
        expressionTextField.setText(item.getExpression());
        expressionTextField.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                expressionTextField.select(0, expressionTextField.getText().length());
            }
        });
        expressionTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                changed();
            }
        });
        // 
        type = item.getType(); // store, needed for create new item
        //
        checkBoxGrouping.setSelected(item.isGrouping());
        changed();
    }

    private void changed() {
        String expression = expressionTextField.getText();
        // OK enabled if we have input...
        setOKActionEnabled(expression.length() > 0);
        // Show errors (Error ist shown in html, so we convert our Messege to html)
        setErrorText(AlternateUtils.toHTML(AlternateGenericPrefixPostfixRegexItem.getErrorText(expression)));
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
    private AlternateGenericPrefixPostfixRegexItem getItem() {
        return new AlternateGenericPrefixPostfixRegexItem(type, expressionTextField.getText(), checkBoxGrouping.isSelected());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JComponent getPreferredFocusedComponent() {
        return expressionTextField;
    }

    /**
     * 
     */
    public static void showDialog(@NotNull String title, @NotNull AlternateGenericPrefixPostfixRegexItem currentItem, @NotNull AbstractDataPanel.Runnable<AlternateGenericPrefixPostfixRegexItem> runnable) {
        AlternateGenericPrefixPostfixRegexItemDialog dialog = new AlternateGenericPrefixPostfixRegexItemDialog(title, currentItem);
        dialog.show();
        if (dialog.getExitCode() == DialogWrapper.OK_EXIT_CODE) {
            AlternateGenericPrefixPostfixRegexItem newItem = dialog.getItem();
            if (!newItem.equals(currentItem) && !newItem.isEmpty()) {
                runnable.run(newItem);
            }
        }
    }
}
